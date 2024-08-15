package com.example.combine;

public class userResponseModel {
    public userResponseModel(String message) {
        this.message = message;
    }

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public userResponseModel() {
    }
}
