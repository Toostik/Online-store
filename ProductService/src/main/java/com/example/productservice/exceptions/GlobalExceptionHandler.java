package com.example.productservice.exceptions;

import com.example.productservice.dto.error.ErrorResponse;
import com.example.productservice.exceptions.category.CategoryNotFoundException;
import com.example.productservice.exceptions.flashsale.FlashNotActiveException;
import com.example.productservice.exceptions.flashsale.FlashSaleNotFoundException;
import com.example.productservice.exceptions.flashsale.FlashSaleReserveException;
import com.example.productservice.exceptions.flashsale.FlashSaleSoldOutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<String> handle(CategoryNotFoundException ex)
    {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(FlashSaleSoldOutException.class)
    public ResponseEntity<ErrorResponse> handleFlashSaleSoldOut(
            FlashSaleSoldOutException ex
    ) {

        ErrorResponse response = ErrorResponse.builder()
                .status(409)
                .error("Conflict")
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(FlashSaleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFlashSaleNotFound(
            FlashSaleNotFoundException ex
    ) {

        ErrorResponse response = ErrorResponse.builder()
                .status(404)
                .error("Not Found")
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler({
            FlashSaleReserveException.class,
            FlashNotActiveException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(
            RuntimeException ex
    ) {

        ErrorResponse response = ErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.badRequest()
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception ex
    ) {

        log.error("Unexpected error", ex);

        ErrorResponse response = ErrorResponse.builder()
                .status(500)
                .error("Internal Server Error")
                .message("Unexpected error")
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.internalServerError()
                .body(response);
    }

}
