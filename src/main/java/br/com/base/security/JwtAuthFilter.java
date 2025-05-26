package br.com.base.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.base.exception.ExceptionBody;
import br.com.base.utils.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtHelper jwtHelper;
	private final ObjectMapper objectMapper;
	private final UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String token = CookieUtils.getCookieValue(request, "jwt");
			boolean isAuthEndpoint = request.getRequestURI().startsWith("/api/v1/auth");
			
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
				var userDetails = userDetailsService.loadUserByUsername(username);
				if (jwtHelper.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} else {
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
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json");

		ExceptionBody errorBody = ExceptionBody.builder()
				.status(HttpStatus.FORBIDDEN.value())
				.error(HttpStatus.FORBIDDEN.name())
				.message(message)
				.path(request.getRequestURI())
				.build();

		response.getWriter().write(objectMapper.writeValueAsString(errorBody));
	}
}