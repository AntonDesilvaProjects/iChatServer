package com.ichat.service;

import com.ichat.command.ArgParser;
import com.ichat.command.Command;
import com.ichat.command.Members;
import com.ichat.command.Weather;
import com.ichat.common.Headers;
import com.ichat.server.MessageBroker;
import com.ichat.server.SocketConnection;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
    List of possible commands:
    -> @members - display a list of ppl on the chat right now
    -> @weather - display weather
            - today
            - 5 - day
            - hourly
    -> @direction [toAddress] - generates a link to GMaps
    -> @cal [computation] - hooks into wolfram
    -> @color [color] - changes color
*/
public class MessageProcessorService extends Service {
    private MessageBroker messageBroker;
    private final Map<String, Command> keyToCommandMap;

    public MessageProcessorService(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
        keyToCommandMap = generateCommandMap();
    }

    public <T> void processMessage(Message<T> message, SocketConnection socketConnection) {
        if (message == null) {
            return;
        }
        Map<String, String> headers = message.getHeaders();
        if (!isValidHeaders(headers)) {
            return; //without valid headers, we won't process message
        }
        String contentType = headers.get(Headers.CONTENT_TYPE);
        if (Headers.ContentType.TEXT.equals(contentType)) {
            if (headers.containsKey(Headers.HANDSHAKE)) {
                //this is a message to the server from client - don't broadcast
                //message to all other clients
                if (StringUtils.isNotBlank(headers.get(Headers.USER))) {
                    socketConnection.setUsername(headers.get(Headers.USER));
                }
                messageBroker.publishSystemMessage(generateChatJoinMessage(socketConnection.getUsername()));
            } else {
                //process message and broadcast to clients
                String messageString = (String) message.getBody();
                messageBroker.publishMessage(message);
                handleCommands(messageString);
            }
        }
    }

    public MessageBroker getMessageBroker() {
        return messageBroker;
    }

    public MessageProcessorService setMessageBroker(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
        return this;
    }

    private boolean isValidHeaders(Map<String, String> headers) {
        if (headers == null) {
            return false;
        }
        if (!headers.containsKey(Headers.CONTENT_TYPE)) {
            return false; //we need the content type to handle the message appropriately
        }
        return true;
    }

    private String generateChatJoinMessage(String username) {
        return String.format("%s has joined the chat!", username);
    }

    private void handleCommands(String message) {
        List<AbstractMap.SimpleEntry<Command, Map<Character, String>>> commandsArgs = getExecutableCommands(message);
        StringBuilder stringBuilder = new StringBuilder();
        commandsArgs.forEach(c -> {
            Object result = c.getKey().process(c.getValue());
            if (result != null) {
               stringBuilder.append(result);
            }
        });
        //send system message
        messageBroker.publishSystemMessage(stringBuilder.toString());
    }

    private List<AbstractMap.SimpleEntry<Command, Map<Character, String>>> getExecutableCommands(String message) {
        //extract commands from text message
        //for example, "check the @weather in nyc and @order me a pizza
        //['@weather in nyc and', '@order me a pizza'] where first string is the command and
        //all the following tokens till the next command are considered arguments
        Pattern pattern = Pattern.compile("(@[^@]*)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(message);
        Command command;
        List<AbstractMap.SimpleEntry<Command, Map<Character, String>>> commandList = new ArrayList<>();
        while (matcher.find()) {
            String commandArgs = matcher.group().trim();//no trailing spaces
            int argsStartIdx = commandArgs.indexOf(' ');
            String commandStr = argsStartIdx < 0 ? commandArgs : commandArgs.substring(0, argsStartIdx);
            command = keyToCommandMap.get(commandStr);
            Map<Character, String> paramArgMap = null;
            if (command != null) {
                if (argsStartIdx > 0) {
                    String args = commandArgs.substring(argsStartIdx).trim();
                    paramArgMap = ArgParser.getParametersAndArguments(args, command.getParameters());
                    if (MapUtils.isEmpty(paramArgMap)) {
                        //if the argument parser cannot find any valid arguments in the arg string
                        //we will pass the entire arg string as a default argument to the command
                        Character defaultParam = command.getDefaultParameter();
                        if (defaultParam != null) {
                            paramArgMap.put(defaultParam, args);
                        }
                    }
                }
                commandList.add(new AbstractMap.SimpleEntry<>(command, paramArgMap));
            }
        }
        return commandList;
    }

    private Map<String, Command> generateCommandMap() {
        Map<String, Command> map = new HashMap<>();
        map.put("@weather", new Weather());
        map.put("@members", new Members(messageBroker));
        return map;
    }
}
