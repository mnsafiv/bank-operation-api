package ru.safonoviv.bankoperationapi.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionRollBack extends RuntimeException{
    private final HttpStatus status;

    public ExceptionRollBack(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }


}
