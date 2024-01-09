package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateException(MethodArgumentNotValidException e) {
        String errorMessage = e.getMessage();
        log.error("Method Argument Not Valid Exception = {}", errorMessage);
        return new ErrorResponse("MethodArgumentNotValidException", errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateException(IllegalArgumentException e) {
        String errorMessage = e.getMessage();
        log.error("Illegal Argument Exception Exception = {}", errorMessage);
        return new ErrorResponse("IllegalArgumentException", errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUndefinedException(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String errorMessage = e.getMessage();
        log.error("Exception = {}", errorMessage, e);
        return new ErrorResponse(errorMessage, e.toString());
    }
}
