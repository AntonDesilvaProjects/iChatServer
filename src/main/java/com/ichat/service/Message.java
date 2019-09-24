package com.ichat.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message<T> implements Serializable {

    public enum BroadcastPolicy {
        ALL, SELECT, NONE
    }

    private static final long serialVersionUID = 6343811905960397349L;

    private T body;
    private Map<String,String> headers;

    private BroadcastPolicy broadcastPolicy = BroadcastPolicy.ALL;
    private List<String> recipients;

    public Message() {
        this.headers = new HashMap<>();
    }

    public Message(T body) {
        this.body = body;
        this.headers = new HashMap<>();
    }

    public Message(T body, Map<String, String> headers) {
        this.body = body;
        this.headers = headers;
    }

    public T getBody() {
        return body;
    }

    public Message<T> setBody(T body) {
        this.body = body;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Message<T> setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public BroadcastPolicy getBroadcastPolicy() {
        return broadcastPolicy;
    }

    public Message<T> setBroadcastPolicy(BroadcastPolicy broadcastPolicy) {
        this.broadcastPolicy = broadcastPolicy;
        return this;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public Message<T> setRecipients(List<String> recipients) {
        this.recipients = recipients;
        return this;
    }

    @Override
    public String toString() {
        return "Message{" +
                "body=" + body +
                ", headers=" + headers +
                '}';
    }
}
