package com.ichat.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message<T> implements Serializable {

    private T body;
    private Map<String,String> headers;
    private static final long serialVersionUID = 6343811905960397349L;

    public Message() {}

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

    @Override
    public String toString() {
        return "Message{" +
                "body=" + body +
                ", headers=" + headers +
                '}';
    }
}
