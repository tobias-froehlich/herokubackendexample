package org.example;

public class DatabaseConflictException extends RuntimeException {

    public DatabaseConflictException(String message) {
        super(message);
    }
}
