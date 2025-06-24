package com.example.pushnotificationdemo.sendnotification;

// MessageRequest.java
public class MessageRequest {
    private Message message;

    public MessageRequest(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
