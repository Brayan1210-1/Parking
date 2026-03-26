package com.cesde.parkingFlow.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cesde.parkingFlow.exception.ErrorResponse;
import com.cesde.parkingFlow.exception.custom.Unauthorized;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(exception = Unauthorized.class)
	public ResponseEntity<ErrorResponse> unauthorizedError(Unauthorized ex){
		ErrorResponse error = new ErrorResponse(
				HttpStatus.UNAUTHORIZED.value(),
				ex.getMessage(),
				LocalDateTime.now()
				);
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
	}
}
