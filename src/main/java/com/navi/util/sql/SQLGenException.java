package com.navi.util.sql;

public class SQLGenException extends RuntimeException {

    private final String message;
    private final Throwable cause;

    public SQLGenException(String message) {
        super(message);
        this.message = message;
        this.cause = null;
    }

    public SQLGenException(Throwable cause) {
        super(cause);
        this.message = null;
        this.cause = cause;
    }

    public SQLGenException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.cause = cause;
    }

    public String message() {
        return this.message;
    }

    public Throwable cause() {
        return this.cause;
    }
}
