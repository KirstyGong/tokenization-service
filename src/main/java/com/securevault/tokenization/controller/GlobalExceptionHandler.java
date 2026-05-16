package com.securevault.tokenization.controller;

import com.securevault.tokenization.dto.ErrorResponse;
import com.securevault.tokenization.exception.EncryptionException;
import com.securevault.tokenization.exception.TokenNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity<ErrorResponse> handleEncryptionException(EncryptionException ex) {
        return ResponseEntity.internalServerError().body(new ErrorResponse("Encryption failed"));
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTokenNotFoundException(TokenNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
