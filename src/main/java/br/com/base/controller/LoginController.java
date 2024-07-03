package br.com.base.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.base.records.LoginRecordInput;
import br.com.base.records.UserRecordInput;
import br.com.base.services.LoginService;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("/api/v1/auth")
public class LoginController {
	
	private final LoginService loginService;
	
	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<Object> login(@Valid @RequestBody LoginRecordInput record) {
		return loginService.login(record);
	}
	
	@PostMapping("/register")
	public ResponseEntity<Object> register(@Valid @RequestBody UserRecordInput record) {
		return loginService.register(record);
	}
	
}