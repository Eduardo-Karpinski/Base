package br.com.base.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

	private final UserDetailsServiceImpl userDetailsService;
	private final ObjectMapper objectMapper;
	private final JwtHelper jwtHelper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			
			String token = CookieUtils.getCookieValue(request, "jwt");
            String username = (token != null && !token.isBlank()) ? jwtHelper.extractUsername(token) : null;

			if (token == null) {
				if (request.getRequestURI().startsWith("/api/v1/auth")) {
					filterChain.doFilter(request, response);
					return;
				}
				
				sendAuthError(response, request, "Token is null");
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
				sendAuthError(response, request, "Invalid token");
				return;
			}

			filterChain.doFilter(request, response);
		} catch (Exception e) {
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