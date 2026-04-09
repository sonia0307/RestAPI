package com.example.demo.exception;

public class DeviceConstraintViolationException extends RuntimeException {
    public DeviceConstraintViolationException(String message) {
        super(message);
    }
}
