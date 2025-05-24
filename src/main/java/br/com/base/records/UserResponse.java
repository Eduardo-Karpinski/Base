package br.com.base.records;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(Long id, String name, String email, Set<String> roles, LocalDateTime creationDate) {}