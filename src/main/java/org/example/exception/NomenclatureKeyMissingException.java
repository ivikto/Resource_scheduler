package org.example.exception;

public class NomenclatureKeyMissingException extends RuntimeException {

    public NomenclatureKeyMissingException(String message) {
        super(message);
    }
    public NomenclatureKeyMissingException(String message, Throwable cause) {
        super(message, cause);
    }
}


