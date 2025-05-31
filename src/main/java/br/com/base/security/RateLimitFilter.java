package br.com.base.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.base.utils.ErrorResponseWriter;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {
	
	private final Map<String, Bucket> buckets;
	private final ErrorResponseWriter errorResponseWriter;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String ip = httpRequest.getRemoteAddr();

		Bucket bucket = buckets.computeIfAbsent(ip, k -> Bucket.builder()
						.addLimit(Bandwidth.builder().capacity(10).refillIntervally(10, Duration.ofSeconds(1)).build())
						.build());

		if (bucket.tryConsume(1)) {
			chain.doFilter(request, response);
		} else {
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			errorResponseWriter.write(httpServletResponse, HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", httpRequest.getRequestURI());
		}
	}
	
}