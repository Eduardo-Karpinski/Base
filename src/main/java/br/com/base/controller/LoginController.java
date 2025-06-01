package br.com.base.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.base.records.LoginRequest;
import br.com.base.services.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LoginController {

	private final LoginService loginService;

	@PostMapping("/login")
	public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
		return loginService.login(loginRequest, response);
	}

	@GetMapping("/validate")
	public ResponseEntity<Object> validateToken(HttpServletRequest request) {
		return loginService.validate(request);
	}

	@PostMapping("/logout")
	public ResponseEntity<Object> logout(HttpServletResponse response) {
		return loginService.logout(response);
	}

}