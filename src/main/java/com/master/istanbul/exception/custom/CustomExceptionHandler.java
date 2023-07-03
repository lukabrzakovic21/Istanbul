package com.master.istanbul.exception.custom;

import com.master.istanbul.exception.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = EmailAlreadyInUse.class)
    protected ResponseEntity<Object> handleEmailAlreadyInUse(RuntimeException exception, WebRequest webRequest) {

        return  handleExceptionInternal(exception, new ErrorResponse(409, exception.getMessage()),
                new HttpHeaders(), HttpStatusCode.valueOf(409), webRequest);
    }
}