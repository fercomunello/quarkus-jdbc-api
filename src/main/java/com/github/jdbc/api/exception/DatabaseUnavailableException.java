package com.github.jdbc.api.exception;

public final class DatabaseUnavailableException extends RuntimeException {

    public DatabaseUnavailableException(final Throwable cause) {
        super(cause);
    }
}