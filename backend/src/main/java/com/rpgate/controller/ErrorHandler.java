package com.rpgate.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validation(MethodArgumentNotValidException e){return error(HttpStatus.BAD_REQUEST,"Validation failed");}
    @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<?> conflict(DataIntegrityViolationException e){String detail=String.valueOf(e.getMostSpecificCause().getMessage()).toLowerCase();if(detail.contains("unique"))return error(HttpStatus.CONFLICT,"A record with the same Army No, I-Card No, pass number, or other unique value already exists");if(detail.contains("check"))return error(HttpStatus.BAD_REQUEST,"One or more values failed database validation");if(detail.contains("foreign key"))return error(HttpStatus.BAD_REQUEST,"The referenced personnel record does not exist");return error(HttpStatus.CONFLICT,"The request conflicts with existing data");}
    @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<?> badRequest(IllegalArgumentException e){return error(HttpStatus.BAD_REQUEST,e.getMessage());}
    @ExceptionHandler(Exception.class) ResponseEntity<?> internal(Exception e){return error(HttpStatus.INTERNAL_SERVER_ERROR,"Internal server error");}
    private ResponseEntity<?> error(HttpStatus status,String message){return ResponseEntity.status(status).body(Map.of("timestamp",Instant.now().toString(),"status",status.value(),"error",status.getReasonPhrase(),"message",message));}
}
