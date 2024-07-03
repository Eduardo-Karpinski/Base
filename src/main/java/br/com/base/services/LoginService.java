package br.com.base.services;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.base.domain.User;
import br.com.base.mappers.UserMapper;
import br.com.base.records.LoginRecordInput;
import br.com.base.records.UserRecordInput;
import br.com.base.repositories.UserRepository;
import br.com.base.security.JwtHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginService {

	private final AuthenticationManager authenticationManager;
	private final JwtHelper jwtHelper;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public LoginService(AuthenticationManager authenticationManager, JwtHelper jwtHelper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtHelper = jwtHelper;
	}

	public ResponseEntity<Object> login(LoginRecordInput record) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(record.email(), record.password()));
		String token = jwtHelper.generateToken(record.email());
	    Optional<User> UserByEmail = userRepository.findByEmail(record.email());
		return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toLoginOutput(UserByEmail.get(), token));
	}
	
	public ResponseEntity<Object> register(UserRecordInput record) {
		if (userRepository.existsByEmail(record.email())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already used");
		}
		User user = User.builder()
				.name(record.name())
				.email(record.email())
				.password(passwordEncoder.encode(record.password()))
				.build();
		userRepository.save(user);
		log.info("Usuario criado com sucesso");
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}