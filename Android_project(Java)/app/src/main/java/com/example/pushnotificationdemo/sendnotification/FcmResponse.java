package com.example.pushnotificationdemo.sendnotification;

// FcmResponse.java
public class FcmResponse {
    private String name;

    public FcmResponse() {
    }

    public FcmResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
