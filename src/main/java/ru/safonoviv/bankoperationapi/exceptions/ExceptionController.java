package ru.safonoviv.bankoperationapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(value = ExceptionRollBack.class)
    public ResponseEntity<Object> exceptionRollback(ExceptionRollBack e) {
        return new ResponseEntity<>(new ResponseException(e.getStatus().value(), e.getMessage()), e.getStatus());
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<Object> exceptionNotFound(NotFoundException e) {
        return new ResponseEntity<>(new ResponseException(e.getStatus().value(), e.getMessage()), e.getStatus());
    }


    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<Object> exceptionNotFound(UsernameNotFoundException e) {
        return new ResponseEntity<>(new ResponseException(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }
}