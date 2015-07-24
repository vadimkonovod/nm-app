package com.epam.lab.news.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.lab.news.dao.AuthorDAO;
import com.epam.lab.news.domain.Author;
import com.epam.lab.news.service.AuthorService;
import com.epam.lab.news.service.exception.EntityNotExistException;

@Service
@Transactional
public class AuthorServiceImpl extends JPAGenericService<Author, Long> implements AuthorService {
    @Autowired
    private AuthorDAO dao;

    @Override
    public Author create(Author entity) {
        entity.setAuthorId(null);
        return dao.insert(entity);
    }

    @Override
    public Author read(Long id) {
        Author author = dao.select(id);
        if (author == null) {
            throw new EntityNotExistException("Author with id=" + id + " doesn't exist");
        }
        return author;
    }

    @Override
    public List<Author> readAll() {
        List<Author> authors = dao.selectAll();
        return authors;
    }

    @Override
    public Author update(Author entity) {
        Author updatedAuthor = null;
        if (read(entity.getAuthorId()) != null) {
            updatedAuthor = dao.update(entity);
        } else {
            throw new EntityNotExistException("Author with id=" + entity.getAuthorId() + " doesn't exist");
        }
        return updatedAuthor;
    }

    @Override
    public void delete(Long id) {
        dao.delete(id);
    }

    @Override
    public Long getAuthorNewsCount(Long id) {
        return dao.getAuthorNewsCount(id);
    }
}
