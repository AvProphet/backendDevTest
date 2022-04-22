package com.reactive.reactivetest.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Products not found");
    }
}
