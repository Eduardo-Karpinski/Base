package br.com.base.records;

import java.time.LocalDateTime;

public record LoginRecordOutput(Long id, String name, LocalDateTime creationDate, String sessionToken) {}