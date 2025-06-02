package br.com.base.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.bucket4j.caffeine.CaffeineProxyManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RateLimitConfig {
	@Bean
	public CaffeineProxyManager<String> rateLimitProxyManager() {
		Caffeine<Object, Object> maximumSize = Caffeine.newBuilder()
				.maximumSize(100_000)
				.removalListener((key, value, cause) -> log.info("[RateLimit] Entry removed from cache: {} (reason: {})", key, cause));
		return new CaffeineProxyManager<>(maximumSize, Duration.ofMinutes(10));
	}
}