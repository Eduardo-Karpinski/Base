package br.com.base.records;

import java.time.LocalDateTime;

public record UserRecordOutput(Long id, String name, LocalDateTime creationDate) {}