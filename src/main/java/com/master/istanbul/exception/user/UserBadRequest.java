package com.master.istanbul.exception.user;

public class UserBadRequest extends RuntimeException{

    public UserBadRequest(String message) {
        super(message);
    }
}
