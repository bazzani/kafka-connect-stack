package com.bevans.kafka.connect.springboot.ffvii.exception;

public class FFVIIException extends RuntimeException {
    public FFVIIException(String s, Throwable e) {
        super(s, e);
    }
}
