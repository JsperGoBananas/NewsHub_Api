package com.jl.newshubapi.handler;

import com.jl.newshubapi.model.dtos.ResponseResult;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseResult> handleValidationException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseResult.errorResult(429, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseResult> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseResult.errorResult(500, ex.getMessage()));
    }
}
