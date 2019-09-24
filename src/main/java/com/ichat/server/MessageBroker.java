package com.ichat.server;

import com.ichat.common.Constants;
import com.ichat.common.Headers;
import com.ichat.service.Message;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MessageBroker {
    private Map<String, SocketConnection> connectedClientMap;
    private ArrayBlockingQueue<Message> messagePipeline;

    private static final int MESSAGE_PIPELINE_SIZE = 1024;

    public MessageBroker() {
        this.connectedClientMap = new ConcurrentHashMap<>();
        this.messagePipeline = new ArrayBlockingQueue<>(MESSAGE_PIPELINE_SIZE);
        broadcastMessages();
    }

    public void publishMessage(Message message) {
        this.messagePipeline.add(message);
    }

    public void publishSystemMessage(String messageStr) {
        if (StringUtils.isEmpty(messageStr)) {
            return;
        }
        Message<String> message = new Message<>();
        Map<String, String> headers = new HashMap<>();
        headers.put(Headers.USER, "System");
        headers.put(Headers.CONTENT_TYPE, Headers.ContentType.TEXT);
        message.setBody(messageStr);
        message.setHeaders(headers);
        this.messagePipeline.add(message);
    }

    private void broadcastMessages() {
        CompletableFuture.runAsync(()->{
            while (true) {
                Message message = null;
                try {
                    message = messagePipeline.take();
                    if (message.getBroadcastPolicy() != Message.BroadcastPolicy.NONE) {
                        Collection<SocketConnection> recipientList = new ArrayList<>();
                        if (message.getBroadcastPolicy() == Message.BroadcastPolicy.SELECT) {
                            for (SocketConnection socketConnection : connectedClientMap.values()) {
                                if (message.getRecipients().contains(socketConnection.getClientId())) {
                                    recipientList.add(socketConnection);
                                }
                            }
                        } else if (message.getBroadcastPolicy() == Message.BroadcastPolicy.ALL) {
                            recipientList = connectedClientMap.values();
                        }
                        //send message to recipients
                        for(SocketConnection socketConnection : recipientList) {
                            socketConnection.sendMessage(message);
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, Constants.THREAD_POOL);
    }

    public void registerSocketConnection(SocketConnection socketConnection) {
        connectedClientMap.put(socketConnection.getClientId(), socketConnection);
    }

    public void unregisterSocketConnection(String clientId) {
        connectedClientMap.remove(clientId);
    }

    public Map<String, SocketConnection> getConnectedClientMap() {
        return connectedClientMap;
    }
}
