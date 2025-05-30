package br.com.base.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

@Configuration
public class JacksonConfig {
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer customizer() {
		return builder -> {
			builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			builder.modulesToInstall(JavaTimeModule.class, ParameterNamesModule.class);
			builder.visibility(PropertyAccessor.FIELD, Visibility.ANY);
		};
	}
}