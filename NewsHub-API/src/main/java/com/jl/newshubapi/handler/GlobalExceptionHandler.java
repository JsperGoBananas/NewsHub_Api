package com.jl.newshubapi.handler;

import com.jl.newshubapi.model.dtos.ResponseResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseResult> handleValidationException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseResult.errorResult(403, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseResult> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseResult.errorResult(500, ex.getMessage()));
    }
}
