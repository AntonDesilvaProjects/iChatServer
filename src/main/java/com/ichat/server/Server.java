package com.ichat.server;

import com.ichat.ServerRunner;
import com.ichat.service.MessageProcessorService;
import com.ichat.service.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private MessageBroker messageBroker;
    private ServerSocket serverSocket;

    private final int DEFAULT_PORT = 3021;

    public Server() throws IOException {
        System.out.println("Starting up server...");
        this.messageBroker = new MessageBroker();
        this.serverSocket = new ServerSocket(DEFAULT_PORT);
    }

    public Server(int port) throws IOException {
        System.out.println("Starting up server...");
        this.messageBroker = new MessageBroker();
        this.serverSocket = new ServerSocket(port);
        MessageProcessorService messageProcessorService = (MessageProcessorService) ServerRunner.SERVICES.get(Service.Services.MESSAGE_PROCESSOR);
        messageProcessorService.setMessageBroker(messageBroker);
    }

    public void startServer() throws IOException {
        System.out.println("Waiting for connections...");
        startListeningForConnections();
    }

    private void startListeningForConnections() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            SocketConnection socketConnection = new SocketConnection(socket, messageBroker);
            messageBroker.registerSocketConnection(socketConnection);
        }
    }

    public MessageBroker getMessageBroker() {
        return messageBroker;
    }
}
