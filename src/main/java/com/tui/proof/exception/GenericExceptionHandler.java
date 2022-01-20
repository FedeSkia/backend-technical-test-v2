package com.tui.proof.exception;

import com.tui.proof.dto.ErrorDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Log4j2
@ControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error(ex);
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return ResponseEntity.badRequest().body(processFieldErrors(fieldErrors));
    }

    @ExceptionHandler(ClientDoesntExists.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValid(ClientDoesntExists ex) {
        log.error(ex);
        return ResponseEntity.badRequest().body(ErrorDto.builder()
                .message(Collections.singletonList(ex.getMessage()))
                .build());
    }

    @ExceptionHandler(AddressNotFound.class)
    public ResponseEntity<ErrorDto> handleAddressNotFound(AddressNotFound ex) {
        log.error(ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorDto.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message(Collections.singletonList(ex.getMessage()))
                        .build());
    }

    private ErrorDto processFieldErrors(List<FieldError> fieldErrors) {
        return ErrorDto.builder()
                .status(BAD_REQUEST)
                .message(fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()))
                .build();
    }

}
