package com.example.authservice.exceptions;

import com.example.authservice.exceptions.token.TokenException;
import com.example.authservice.exceptions.user.PasswordWrongException;
import com.example.authservice.exceptions.user.UserRegisteredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserRegisteredException.class)
    public ResponseEntity<String> handleUserExists(UserRegisteredException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());

    }
    @ExceptionHandler(PasswordWrongException.class)
    public ResponseEntity<String> handlePasswordWrong(PasswordWrongException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<String> handleTokenException(TokenException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
