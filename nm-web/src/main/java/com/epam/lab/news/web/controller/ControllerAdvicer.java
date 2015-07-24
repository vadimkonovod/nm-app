package com.epam.lab.news.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.epam.lab.news.service.exception.EntityNotExistException;
import com.epam.lab.news.service.exception.ServiceException;

@ControllerAdvice
public class ControllerAdvicer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAdvicer.class);

    @Autowired
    private MessageSource messageSource; 

    private String getMessage(String message) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(message, null, locale);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody public ErrorInfo notValidExceptionHandler(MethodArgumentNotValidException e) {
        LOGGER.error("Validation error", e);
        List<FieldError> fieldErrors =  e.getBindingResult().getFieldErrors();
        List<String> validationErrors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            validationErrors.add(getMessage(fieldError.getDefaultMessage()));
        }
        return new ErrorInfo(getMessage("error.validation"), validationErrors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody public ErrorInfo notValidEntityExceptionHandler(EntityNotFoundException e) {
        LOGGER.error("Validation error", e);
        return new ErrorInfo(e.getMessage());
    }

    @ExceptionHandler(EntityNotExistException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody public ErrorInfo notValidEntityExceptionHandler(EntityNotExistException e) {
        LOGGER.error("Validation error", e);
        return new ErrorInfo(e.getMessage());
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody public ErrorInfo typeMismatchExceptionHandler(TypeMismatchException e) {
        String message = e.getMessage();
        LOGGER.error(message, e);
        return new ErrorInfo(message);
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody public ErrorInfo validationExceptionHandler(NumberFormatException e) {
        String message = e.getMessage();
        LOGGER.error(message, e);
        return new ErrorInfo(message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody public ErrorInfo missingParameterExceptionHandler(MissingServletRequestParameterException e) {
        String message = e.getMessage();
        LOGGER.error(message, e);
        return new ErrorInfo(message);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody public ErrorInfo serviceExceptionHandler(ServiceException e) {
        String message = e.getMessage();
        LOGGER.error(message, e);
        return new ErrorInfo(message);
    }

    @ExceptionHandler(HttpMediaTypeException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ResponseBody public ErrorInfo mediaTypeExceptionHandler(HttpMediaTypeException e) {
        String message = e.getMessage();
        LOGGER.error(message, e);
        return new ErrorInfo("Media type not supported: " + message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody public ErrorInfo generalExceptionHandler(Exception e) {
        LOGGER.error("Internal server error", e);
        return new ErrorInfo(getMessage("error.service"));
    }
}
