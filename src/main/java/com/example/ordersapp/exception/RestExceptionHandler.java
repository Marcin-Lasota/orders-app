package com.example.ordersapp.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Order
@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse constraintViolationExceptionHandler(ConstraintViolationException ex) {
        log.error("Constraint validation exception", ex);
        return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse validationExceptionHandler(ValidationException ex) {
        log.error("Validation exception", ex);
        return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse entityNotFoundHandler(EntityNotFoundException ex) {
        log.error("Entity not found", ex);
        return ErrorResponse.create(ex, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponse handleInternalServerError(RuntimeException ex) {
        String message = "Internal server error";
        log.error(message, ex);
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse optimisticLockingExceptionHandler(ObjectOptimisticLockingFailureException ex) {
        log.error("Optimistic lock exception: ", ex);
        String message = "The same data has been modified by another user. Please refresh the page and try again.";
        return ErrorResponse.create(ex, HttpStatus.CONFLICT, message);
    }
}
