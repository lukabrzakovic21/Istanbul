package com.master.istanbul.exception.custom;

public class UnauthorizedException extends RuntimeException  {

    public UnauthorizedException(String message) {
        super(message);
    }
}
