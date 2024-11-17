package br.com.base.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.base.domain.User;
import br.com.base.mappers.UserMapper;
import br.com.base.records.LoginRecordInput;
import br.com.base.security.CustomUserDetails;
import br.com.base.security.JwtHelper;

@Service
public class LoginService {

	private final AuthenticationManager authenticationManager;
	private final JwtHelper jwtHelper;

	public LoginService(AuthenticationManager authenticationManager, JwtHelper jwtHelper) {
		this.authenticationManager = authenticationManager;
		this.jwtHelper = jwtHelper;
	}

	public ResponseEntity<Object> login(LoginRecordInput record) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(record.email(), record.password()));
	    User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
		String token = jwtHelper.generateToken(user);
		return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toLoginOutput(user, token));
	}
	
}