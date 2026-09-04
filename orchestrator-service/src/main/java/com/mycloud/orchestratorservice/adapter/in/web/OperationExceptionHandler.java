package com.mycloud.orchestratorservice.adapter.in.web;

import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

@RestControllerAdvice
public class OperationExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(NoSuchElementException exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(Exception exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(RestClientResponseException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public Map<String, String> handleUpstreamFailure(RestClientResponseException exception) {
        return Map.of("message", "upstream request failed: " + exception.getStatusCode());
    }
}
