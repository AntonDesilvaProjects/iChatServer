package com.ichat.server;

import com.ichat.ServerRunner;
import com.ichat.service.Message;
import com.ichat.service.MessageProcessorService;
import com.ichat.service.Service;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class SocketConnection {

    private Socket socket;
    private String clientId;
    private String username;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private MessageBroker messageBroker;
    private MessageProcessorService messageProcessorService;

    public SocketConnection(Socket socket, MessageBroker messageBroker) throws IOException {
        this.socket = socket;
        this.messageBroker = messageBroker;
        init();
        startListeningToMessages();
    }

    private void init() throws IOException {
        System.out.print("Client connection initiated...");
        objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        objectOutputStream.flush();
        objectInputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        clientId = RandomStringUtils.random(7, true, true);
        username = clientId;
        messageProcessorService = (MessageProcessorService) ServerRunner.SERVICES.get(Service.Services.MESSAGE_PROCESSOR);
        messageProcessorService.setMessageBroker(messageBroker);
        System.out.println("Connection established. Assigned client id " + clientId + " to connection....");
    }

    private void startListeningToMessages() {
        CompletableFuture.runAsync(() -> {
           System.out.println("\tListening for messages from " + clientId);
           while (true) {
               try {
                   Message<String> incomingMessage = (Message<String>) objectInputStream.readObject();
                   messageProcessorService.processMessage(incomingMessage, this);
                   System.out.println("\tReceived Message: " + incomingMessage);
               } catch (IOException e) {
                   System.out.println("Connection ended abruptly...cleaning up & closing connection");
                   cleanUp();
                   messageBroker.publishSystemMessage(username + " has left the chat!");
                   break;
               } catch (ClassNotFoundException e) {
                   e.printStackTrace();
               }
           }
        });
    }

    public void sendMessage(Message message) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(Message message) {
        messageBroker.publishMessage(message);
    }

    public Socket getSocket() {
        return socket;
    }

    public SocketConnection setSocket(Socket socket) {
        this.socket = socket;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public SocketConnection setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public SocketConnection setObjectInputStream(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
        return this;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public SocketConnection setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
        return this;
    }

    public MessageBroker getMessageBroker() {
        return messageBroker;
    }

    public SocketConnection setMessageBroker(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
        return this;
    }

    private void cleanUp() {
        try {
            socket.close();
            objectInputStream.close();
            objectOutputStream.close();
            messageBroker.unregisterSocketConnection(clientId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public SocketConnection setUsername(String username) {
        this.username = username;
        return this;
    }

    public MessageProcessorService getMessageProcessorService() {
        return messageProcessorService;
    }

    public SocketConnection setMessageProcessorService(MessageProcessorService messageProcessorService) {
        this.messageProcessorService = messageProcessorService;
        return this;
    }
}
