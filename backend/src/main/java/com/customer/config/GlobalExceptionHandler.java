package com.customer.config;

import com.customer.model.dto.ApiResponse;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
          .getFieldErrors()
          .forEach(error ->
              errors.put(
                  error.getField(),
                  error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(new ApiResponse(400, "Validation failed", errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrity(
            DataIntegrityViolationException ex) {

        String rootMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        Map<String, String> errors = new HashMap<>();

        if (rootMessage != null && (rootMessage.toLowerCase().contains("unique") || rootMessage.toLowerCase().contains("duplicate") || rootMessage.toLowerCase().contains("constraint"))) {
            // user-friendly, validation-like response for unique constraint violations
            errors.put("duplicate", "Customer with same firstName, lastName and dateOfBirth already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(400, "Validation failed", errors));
        }

        errors.put("database", rootMessage == null ? "Data integrity violation" : rootMessage);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(500, "Database error", errors));
    }

}
