package com.articoding.error;

public class RestError extends RuntimeException  {

    private final String restMessage;

    public RestError(String restMessage) {
        this.restMessage = restMessage;
    }

    public String getRestMessage() {
        return restMessage;
    }
}
