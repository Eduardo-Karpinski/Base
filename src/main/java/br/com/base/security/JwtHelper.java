package br.com.base.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import br.com.base.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtHelper {
	
    private final Integer MINUTES;
    private final SecretKey SECRET_KEY;

    public JwtHelper(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") Integer jwtExpiration) {
    	this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.MINUTES = jwtExpiration;
    }
	
	public String generateToken(User user) {
		var now = Instant.now();
		return Jwts.builder()
				.subject(user.getEmail())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(MINUTES, ChronoUnit.MINUTES)))
				.claim("roles", user.getRoles())
				.signWith(SECRET_KEY)
				.compact();
	}

	public String extractUsername(String token) {
		return getTokenBody(token).getSubject();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	public Claims getTokenBody(String token) {
		try {
			return Jwts.parser()
					.verifyWith(SECRET_KEY)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (ExpiredJwtException e) {
	        return e.getClaims();
	    }
	}

	public boolean isTokenExpired(String token) {
		try {
			Claims claims = getTokenBody(token);
			return claims.getExpiration().before(new Date());
		} catch (Exception e) {
			return true;
		}
	}
	
	public int getExpirationInSeconds() {
	    return MINUTES * 60;
	}
	
}