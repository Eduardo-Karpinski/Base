package br.com.base.filter;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.base.security.JwtHelper;
import br.com.base.security.UserDetailsServiceImpl;
import br.com.base.utils.CookieUtils;
import br.com.base.utils.ErrorResponseWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Order(1)
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtHelper jwtHelper;
	private final ErrorResponseWriter errorResponseWriter;
	private final UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String token = CookieUtils.getCookieValue(request, "jwt");
			boolean isAuthEndpoint = request.getRequestURI().equals("/api/v1/auth/login");
			
			if (isAuthEndpoint) {
				filterChain.doFilter(request, response);
				return;
			}

			if (token == null || token.isBlank()) {
				sendAuthError(response, request, "Token is null or empty");
				return;
			}

			if (jwtHelper.isTokenExpired(token)) {
				sendAuthError(response, request, "Token is expired");
				return;
			}

			String username = jwtHelper.extractUsername(token);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				if (!jwtHelper.validateToken(token, userDetails)) {
					sendAuthError(response, request, "Invalid token");
					return;
				}
			}

			filterChain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			sendAuthError(response, request, e.getMessage());
		}
	}

	private void sendAuthError(HttpServletResponse response, HttpServletRequest request, String message) throws IOException {
		errorResponseWriter.write(response, HttpStatus.FORBIDDEN, message, request.getRequestURI());
	}
}