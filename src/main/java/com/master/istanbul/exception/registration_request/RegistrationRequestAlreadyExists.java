package com.master.istanbul.exception.registration_request;

public class RegistrationRequestAlreadyExists extends RuntimeException {
    public RegistrationRequestAlreadyExists(String message) {
        super(message);
    }
}
