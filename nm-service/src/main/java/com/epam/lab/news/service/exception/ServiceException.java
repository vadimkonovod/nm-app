package com.epam.lab.news.service.exception;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 7780089249138234905L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable e) {
        super(message, e);
    }

    public ServiceException(Throwable e) {
        super(e);
    }
}