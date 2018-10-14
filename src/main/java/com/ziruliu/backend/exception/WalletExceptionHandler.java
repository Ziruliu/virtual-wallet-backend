package com.ziruliu.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

@ControllerAdvice
public class WalletExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<WalletErrorResponse> handleException(Exception exc) {

        WalletErrorResponse error = new WalletErrorResponse();
        SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(localDateFormat.format(new Date()));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
