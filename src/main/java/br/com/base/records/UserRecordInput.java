package br.com.base.records;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRecordInput(
		@NotBlank String name,
		@NotBlank @Email String email,
		@NotBlank String password,
		@NotNull Boolean isAdmin,
		@NotNull MultipartFile image) {}