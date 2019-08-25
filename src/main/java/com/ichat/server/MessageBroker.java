package com.ichat.server;

import com.ichat.common.Headers;
import com.ichat.service.Message;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
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
                    for (SocketConnection socketConnection : connectedClientMap.values()) {
                        socketConnection.sendMessage(message);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void registerSocketConnection(SocketConnection socketConnection) {
        connectedClientMap.put(socketConnection.getClientId(), socketConnection);
    }

    public void unregisterSocketConnection(String clientId) {
        connectedClientMap.remove(clientId);
    }

}
