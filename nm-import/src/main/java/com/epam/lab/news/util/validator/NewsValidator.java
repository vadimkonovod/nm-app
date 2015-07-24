package com.epam.lab.news.util.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.epam.lab.news.exception.ValidationException;

@Component
public class NewsValidator {
    @Autowired
    private Validator validator;

    @Autowired
    private MessageSource messageSource; 

    public <T> void isValid(T object) {
        if (object == null) {
            throw new ValidationException("News may not be null");
        }
        if (validator.validate(object).size() != 0) {
            List<String> validationErrors = new ArrayList<>();
            Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);
            for (ConstraintViolation<T> c : constraintViolations) {
                validationErrors.add(getMessage(c.getMessage()));
            }
            throw new ValidationException(validationErrors.toString());
        }
    }

    private String getMessage(String message) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(message, null, locale);
    }
}