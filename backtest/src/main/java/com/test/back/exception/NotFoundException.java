package com.test.back.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Products not found");
    }
}
