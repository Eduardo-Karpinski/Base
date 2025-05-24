package br.com.base.records;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record UserUpdateRequest(
		@NotBlank String name,
		@NotBlank @Email String email,
		@NotEmpty Set<String> roles) {}
