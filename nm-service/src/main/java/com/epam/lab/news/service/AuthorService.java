package com.epam.lab.news.service;

import com.epam.lab.news.domain.Author;

public interface AuthorService extends GenericService<Author, Long> {
    Long getAuthorNewsCount(Long id);
}
