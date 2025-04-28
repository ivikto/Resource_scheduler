package org.example.exception;

public class InvalidJsonDataException extends RuntimeException {

    public InvalidJsonDataException(String message) {
        super(message);
    }

    public InvalidJsonDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

