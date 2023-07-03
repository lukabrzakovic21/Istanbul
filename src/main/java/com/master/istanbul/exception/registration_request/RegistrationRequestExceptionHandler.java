package com.master.istanbul.exception.registration_request;

import com.master.istanbul.exception.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RegistrationRequestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = RegistrationRequestNotFoundException.class)
    protected ResponseEntity<Object> handleRegistrationRequestNotFoundException
            (RuntimeException exception, WebRequest webRequest) {

        return  handleExceptionInternal(exception, new ErrorResponse(404, exception.getMessage()),
                new HttpHeaders(), HttpStatusCode.valueOf(404), webRequest);
    }

    @ExceptionHandler(value = RegistrationRequestAlreadyExists.class)
    protected ResponseEntity<Object> handleRegistrationRequestAlreadyExists(RuntimeException exception, WebRequest webRequest) {

        return  handleExceptionInternal(exception, new ErrorResponse(409, exception.getMessage()),
                new HttpHeaders(), HttpStatusCode.valueOf(409), webRequest);
    }

    @ExceptionHandler(value = RegistrationRequestBadRequest.class)
    protected ResponseEntity<Object> handleRegistrationRequestBadRequest(RuntimeException exception, WebRequest webRequest) {

        return  handleExceptionInternal(exception, new ErrorResponse(400, exception.getMessage()),
                new HttpHeaders(), HttpStatusCode.valueOf(400), webRequest);
    }
}
