package br.com.base.exception;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ExceptionBody(
    LocalDateTime timestamp,
    Integer status,
    String error,
    String message,
    String path,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<String> errors) {
    public ExceptionBody(Integer status, String error, String message, String path, List<String> errors) {
        this(LocalDateTime.now(), status, error, message, path, errors);
    }

    public ExceptionBody(Integer status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }
}