package com.epam.lab.news.exception;

public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = -4448607670647220580L;

    public ValidationException() {
    }

    public ValidationException(String s) {
        super(s);
    }

    public ValidationException(Throwable t) {
        super(t);
    }

    public ValidationException(String s, Throwable t) {
        super(s, t);
    }
}
