package com.worktrack.employee.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Automatically intercepts any exception thrown by any Controller in this microservice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "Internal Infrastructure Fault");
        errorMap.put("message", ex.getMessage());
        errorMap.put("status", String.valueOf(HttpStatus.NOT_FOUND.value()));

        System.out.println("🚨 GLOBAL EXCEPTION INTERCEPTOR -> Gracefully handled: " + ex.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "Parameter Binding Mismatch");
        errorMap.put("message", "Ensure compilation flags or explicit bindings match: " + ex.getMessage());
        errorMap.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));

        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }
}