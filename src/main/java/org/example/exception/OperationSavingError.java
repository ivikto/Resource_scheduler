package org.example.exception;

public class OperationSavingError extends RuntimeException {
    public OperationSavingError(String message) {
        super(message);
    }

    public OperationSavingError(String message, Throwable cause) {
        super(message, cause);
    }
}
