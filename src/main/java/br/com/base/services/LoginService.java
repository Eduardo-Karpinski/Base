package br.com.base.services;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.base.domain.User;
import br.com.base.mappers.UserMapper;
import br.com.base.records.LoginRequest;
import br.com.base.repositories.UserRepository;
import br.com.base.security.CustomUserDetails;
import br.com.base.security.JwtHelper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

	private final JwtHelper jwtHelper;
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;

	public ResponseEntity<Object> login(LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
		User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
		String token = jwtHelper.generateToken(user);
		return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toLoginResponse(user, token));
	}

	public ResponseEntity<Object> validate(String token) {
		boolean isTokenExpired = jwtHelper.isTokenExpired(token);

		if (isTokenExpired) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Expired token");
		}

		String username = jwtHelper.extractUsername(token);

		Optional<User> optional = userRepository.findByEmail(username);

		if (optional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toLoginResponse(optional.get(), token));
	}

}