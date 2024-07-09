package br.com.base.services;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import br.com.base.domain.User;
import br.com.base.mappers.UserMapper;
import br.com.base.records.LoginRecordInput;
import br.com.base.repositories.UserRepository;
import br.com.base.security.JwtHelper;

@Service
public class LoginService {

	private final AuthenticationManager authenticationManager;
	private final JwtHelper jwtHelper;
	private final UserRepository userRepository;

	public LoginService(AuthenticationManager authenticationManager, JwtHelper jwtHelper, UserRepository userRepository) {
		this.userRepository = userRepository;
		this.authenticationManager = authenticationManager;
		this.jwtHelper = jwtHelper;
	}

	public ResponseEntity<Object> login(LoginRecordInput record) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(record.email(), record.password()));
		String token = jwtHelper.generateToken(record.email());
	    Optional<User> UserByEmail = userRepository.findByEmail(record.email());
		return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toLoginOutput(UserByEmail.get(), token));
	}
	
}