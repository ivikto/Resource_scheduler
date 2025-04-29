package org.example.exception;

public class OperationKitFoundError extends RuntimeException {
    public OperationKitFoundError(String message) {
        super(message);
    }
}
