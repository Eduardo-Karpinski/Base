package br.com.base.filter;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.base.utils.CookieUtils;
import br.com.base.utils.ErrorResponseWriter;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.caffeine.CaffeineProxyManager;
import io.github.bucket4j.distributed.proxy.RemoteBucketBuilder;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

	private final ErrorResponseWriter errorResponseWriter;
	private final CaffeineProxyManager<String> caffeineProxyManager;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		Optional<Cookie> cookie = CookieUtils.getCookie(httpRequest, "jwt");
		String key = cookie.map(Cookie::getValue).orElse(httpRequest.getRemoteAddr());

		Bucket bucket = resolveBucket(key);
		
		if (bucket.tryConsume(1)) {
			chain.doFilter(request, response);
		} else {
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			errorResponseWriter.write(httpServletResponse, HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", httpRequest.getRequestURI());
		}
		
		int totalIps = caffeineProxyManager.getCache().asMap().size();
		log.info("[RateLimit] Currently monitored IPs in cache: {}", totalIps);
	}
	
	private Bucket resolveBucket(String key) {
		Supplier<BucketConfiguration> configSupplier = () -> BucketConfiguration.builder()
				.addLimit(Bandwidth.builder()
						.capacity(10)
						.refillIntervally(10, Duration.ofSeconds(5))
						.build())
				.build();

		RemoteBucketBuilder<String> builder = caffeineProxyManager.builder();
		return builder.build(key, configSupplier);
	}

}