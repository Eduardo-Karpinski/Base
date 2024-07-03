package br.com.base.exception;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionBody {

	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();
	private Integer status;
	private String error;
	private String message;
	private String path;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<String> errors;

}