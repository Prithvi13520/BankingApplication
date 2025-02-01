package com.app.banking.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.angus.mail.util.MailConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.app.banking.constants.Constants;
import com.app.banking.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

     @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
         
        
        ResponseEntity<ErrorResponse> responseEntity = new ResponseEntity<>(
        ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .message("Validation Failed")
        .status(ex.getStatusCode().value())
        .path(request.getRequestURI())
        .details(errors)
        .build(),ex.getStatusCode());
        
        return responseEntity;
    }

    @ExceptionHandler({MailException.class,MailConnectException.class,MailAuthenticationException.class,MailParseException.class,MailSendException.class})
    public ResponseEntity<ErrorResponse> handleMailExceptions(Exception ex,HttpServletRequest request)
    {       
        ResponseEntity<ErrorResponse> responseEntity = new ResponseEntity<>(
        ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .message(Constants.MAIL_EXCEPTION_MESSAGE)
        .status(Integer.parseInt(Constants.MAIL_EXCEPTION_CODE))
        .path(request.getRequestURI())
        .details(Map.of(ex.getClass().getName(), ex.getMessage()))
        .build(),HttpStatus.INTERNAL_SERVER_ERROR);
        
        return responseEntity;
    }

}
