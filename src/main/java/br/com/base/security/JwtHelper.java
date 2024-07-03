package br.com.base.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtHelper {
	
	private final SecretKey SECRET_KEY;
    private final Integer MINUTES;

    public JwtHelper(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") Integer jwtExpiration) {
    	this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.MINUTES = jwtExpiration;
    }
	
	public String generateToken(String email) {
		var now = Instant.now();
		return Jwts.builder()
				.subject(email)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(MINUTES, ChronoUnit.MINUTES)))
				.signWith(SECRET_KEY)
				.compact();
	}

	public String extractUsername(String token) throws Exception {
		return getTokenBody(token).getSubject();
	}

	public Boolean validateToken(String token, UserDetails userDetails) throws Exception {
		final String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private Claims getTokenBody(String token) throws Exception {
		return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
	}

	private boolean isTokenExpired(String token) throws Exception {
		Claims claims = getTokenBody(token);
		return claims.getExpiration().before(new Date());
	}
	
}