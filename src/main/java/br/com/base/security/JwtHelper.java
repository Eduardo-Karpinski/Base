package br.com.base.security;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import br.com.base.domain.User;

@Component
public class JwtHelper {
	
    private final Integer MINUTES;
    private final byte[] SECRET_KEY;

    public JwtHelper(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") Integer jwtExpiration) {
    	this.SECRET_KEY = secret.getBytes(StandardCharsets.UTF_8);
        this.MINUTES = jwtExpiration;
    }
	
	public String generateToken(User user) {
		try {
			Instant now = Instant.now();

			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
					.subject(user.getEmail())
					.issueTime(Date.from(now))
					.expirationTime(Date.from(now.plus(MINUTES, ChronoUnit.MINUTES)))
					.claim("roles", user.getRoles())
					.build();

			SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

			signedJWT.sign(new MACSigner(SECRET_KEY));
			return signedJWT.serialize();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public String extractUsername(String token) {
		return getTokenBody(token).getSubject();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	public JWTClaimsSet getTokenBody(String token) {
		try {
			SignedJWT jwt = SignedJWT.parse(token);
			return jwt.getJWTClaimsSet();
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
	    }
	}

	public boolean isTokenExpired(String token) {
		try {
			Date expiration = getTokenBody(token).getExpirationTime();
			return expiration.before(new Date());
		} catch (Exception e) {
			return true;
		}
	}

}