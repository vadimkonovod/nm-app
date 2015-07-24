package com.epam.lab.news.service.exception;

public class EntityNotExistException extends ServiceException {
    private static final long serialVersionUID = 7780089249138234905L;

    public EntityNotExistException(String message) {
        super(message);
    }

    public EntityNotExistException(String message, Throwable e) {
        super(message, e);
    }

    public EntityNotExistException(Throwable e) {
        super(e);
    }
}