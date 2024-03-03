package ru.safonoviv.bankoperationapi.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ResponseException {
    private int status;
    private String message;
    private Date timestamp;

    public ResponseException(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}
