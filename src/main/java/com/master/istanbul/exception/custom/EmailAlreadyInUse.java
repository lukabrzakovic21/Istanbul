package com.master.istanbul.exception.custom;

public class EmailAlreadyInUse extends RuntimeException {

    public EmailAlreadyInUse(String message) {
        super(message);
    }
}
