package br.com.base.records;

import java.time.LocalDateTime;

public record UserResponse(Long id, String name, LocalDateTime creationDate) {}