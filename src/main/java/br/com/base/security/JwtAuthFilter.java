package br.com.base.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.base.exception.ExceptionBody;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final UserDetailsServiceImpl userDetailsService;
	private final ObjectMapper objectMapper;
	private final JwtHelper jwtHelper;

	public JwtAuthFilter(UserDetailsServiceImpl userDetailsService, ObjectMapper objectMapper, JwtHelper jwtHelper) {
		this.userDetailsService = userDetailsService;
		this.objectMapper = objectMapper;
		this.jwtHelper = jwtHelper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			String authHeader = request.getHeader("Authorization");
			String token = null;
			String username = null;
			
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				token = authHeader.substring(7);
				username = jwtHelper.extractUsername(token);
			}

			if (token == null) {
				if (request.getRequestURI().startsWith("/api/v1/auth")) {
					filterChain.doFilter(request, response);
					return;
				}
				
				returnError(request, response, "Token is null");
				return;
			}

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				if (jwtHelper.validateToken(token, userDetails)) {
//					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					filterChain.doFilter(request, response);
					return;
				}
				returnError(request, response, "Invalid token");
				return;
			}

			filterChain.doFilter(request, response);
		} catch (Exception e) {
			returnError(request, response, e.getMessage());
		}
	}

	private void returnError(HttpServletRequest request, HttpServletResponse response, String message) throws IOException, JsonProcessingException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json");

		ExceptionBody body = ExceptionBody.builder()
				.status(HttpStatus.FORBIDDEN.value())
				.error(HttpStatus.FORBIDDEN.name())
				.message(message)
				.path(request.getRequestURI())
				.build();

		response.getWriter().write(objectMapper.writeValueAsString(body));
	}
}