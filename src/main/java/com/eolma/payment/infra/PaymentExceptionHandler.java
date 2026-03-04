package com.eolma.payment.infra;

import com.eolma.common.exception.EolmaException;
import com.eolma.common.exception.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@Slf4j
@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(EolmaException.class)
    public ProblemDetail handleEolmaException(EolmaException e) {
        ErrorType errorType = e.getErrorType();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.valueOf(errorType.getStatus()),
                e.getDetail()
        );
        problemDetail.setType(URI.create(errorType.getType()));
        problemDetail.setTitle(errorType.getTitle());

        if (errorType.getStatus() >= 500) {
            log.error("Server error: type={}, detail={}", errorType.getType(), e.getDetail(), e);
        } else {
            log.warn("Client error: type={}, detail={}", errorType.getType(), e.getDetail());
        }
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation failed");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, detail);
        problemDetail.setType(URI.create(ErrorType.INVALID_REQUEST.getType()));
        problemDetail.setTitle(ErrorType.INVALID_REQUEST.getTitle());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception e) {
        log.error("Unexpected error", e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
        problemDetail.setType(URI.create(ErrorType.INTERNAL_ERROR.getType()));
        problemDetail.setTitle(ErrorType.INTERNAL_ERROR.getTitle());
        return problemDetail;
    }
}
