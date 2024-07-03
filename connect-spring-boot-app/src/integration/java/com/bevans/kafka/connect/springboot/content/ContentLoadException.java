package com.bevans.kafka.connect.springboot.content;

public class ContentLoadException extends Exception {
    public ContentLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
