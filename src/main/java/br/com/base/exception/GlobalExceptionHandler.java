package br.com.base.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<Object> handleAccessDenied(NoResourceFoundException ex, HttpServletRequest request) {
		ExceptionBody body = new ExceptionBody(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(), ex.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Object> handleAccessDenied(BadCredentialsException ex, HttpServletRequest request) {
		ExceptionBody body = new ExceptionBody(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(), ex.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		ExceptionBody body = new ExceptionBody(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.name(), ex.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
	}
	
	@ExceptionHandler(InternalAuthenticationServiceException.class)
	public ResponseEntity<Object> handleInternalAuthenticationService(InternalAuthenticationServiceException ex, HttpServletRequest request) {
		ExceptionBody body = new ExceptionBody(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(), ex.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Object> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
		ExceptionBody body = new ExceptionBody(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(), ex.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleAccessDenied(ConstraintViolationException ex, HttpServletRequest request) {
		List<String> errors = ex.getConstraintViolations().stream()
				.map(error -> error.getInvalidValue() + ": " + error.getMessage())
				.collect(Collectors.toList());
		ExceptionBody body = new ExceptionBody(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(), "Validation errors occurred", request.getRequestURI(), errors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
		List<String> errors = ex.getBindingResult().getAllErrors().stream().map(error -> {
			if (error instanceof FieldError fieldError) {
				return fieldError.getField() + ": " + error.getDefaultMessage();
			} else {
				return error.getObjectName() + ": " + error.getDefaultMessage();
			}
		}).collect(Collectors.toList());

		ExceptionBody body = new ExceptionBody(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(), "Validation errors occurred", request.getRequestURI(), errors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAllExceptions(Exception ex, HttpServletRequest request) {
		ex.printStackTrace();
		ExceptionBody body = new ExceptionBody(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.name(), ex.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}

}