package br.com.base.records;

import java.time.LocalDateTime;
import java.util.Set;

public record LoginResponse(Long id, String name, String email, LocalDateTime creationDate, Set<String> roles) {}