package br.com.base.services;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
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
import br.com.base.utils.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

	private final JwtHelper jwtHelper;
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;

	public ResponseEntity<Object> login(LoginRequest loginRequest, HttpServletResponse response) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
		User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
		String token = jwtHelper.generateToken(user);
	    response.addHeader(HttpHeaders.SET_COOKIE, CookieUtils.createResponseCookie(token).toString());
		return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toUserResponse(user));
	}

	public ResponseEntity<Object> validate(HttpServletRequest request) {
		Optional<Cookie> cookie = CookieUtils.getCookie(request, "jwt");

		if (cookie.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing token");
		}

		String token = cookie.get().getValue();
		
		boolean isTokenExpired = jwtHelper.isTokenExpired(token);

		if (isTokenExpired) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Expired token");
		}

		String username = jwtHelper.extractUsername(token);

		Optional<User> optional = userRepository.findByEmail(username);

		if (optional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toUserResponse(optional.get()));
	}
	
	public ResponseEntity<Object> logout(HttpServletResponse response) {
	    response.addHeader(HttpHeaders.SET_COOKIE, CookieUtils.createResponseCookie(null).toString());
	    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}