package com.example.pushnotificationdemo;

public class UserInfo {
    public String userName;
    public String userToken;

    public UserInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(UserInfo.class)
    }

    public UserInfo(String userName, String userToken) {
        this.userName = userName;
        this.userToken = userToken;
    }
}
