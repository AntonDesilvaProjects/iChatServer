package com.ichat;

import com.ichat.server.Server;
import com.ichat.service.MessageProcessorService;
import com.ichat.service.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerRunner {
    public static Map<Service.Services,Service> SERVICES;
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        initializeServices(server);
        server.startServer();
    }
    private static void initializeServices(Server server) {
        SERVICES = new HashMap<>();
        SERVICES.put(Service.Services.MESSAGE_PROCESSOR, new MessageProcessorService(server.getMessageBroker()));
    }
}
