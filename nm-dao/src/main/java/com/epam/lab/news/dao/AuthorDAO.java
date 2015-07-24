package com.epam.lab.news.dao;

import com.epam.lab.news.domain.Author;

public interface AuthorDAO extends GenericDAO<Author, Long> {
    Long getAuthorNewsCount(Long id);
}
