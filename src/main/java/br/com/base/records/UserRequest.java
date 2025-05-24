package br.com.base.records;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserRequest(
		@NotBlank String name,
		@NotBlank @Email String email,
		@NotEmpty Set<String> roles,
		@NotBlank @Size(min = 8, max = 32) String password) {}