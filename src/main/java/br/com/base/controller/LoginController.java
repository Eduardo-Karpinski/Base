package br.com.base.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.base.records.LoginRequest;
import br.com.base.services.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LoginController {
	
	private final LoginService loginService;
	
	@PostMapping("/login")
	public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {
		return loginService.login(loginRequest);
	}
	
	@GetMapping("/validate")
	public ResponseEntity<Object> validateToken(HttpServletRequest request) {
	    String authHeader = request.getHeader("Authorization");

	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
	    }

	    String token = authHeader.substring(7);

	    return loginService.validate(token);
	}
	
}