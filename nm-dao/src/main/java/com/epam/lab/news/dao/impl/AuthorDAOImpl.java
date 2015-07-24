package com.epam.lab.news.dao.impl;

import java.math.BigDecimal;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.epam.lab.news.dao.AuthorDAO;
import com.epam.lab.news.domain.Author;

@Repository
public class AuthorDAOImpl extends JPAGenericDAO<Author, Long> implements AuthorDAO {

    @Override
    public Long getAuthorNewsCount(Long id) {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM NEWS_AUTHORS WHERE NEWS_AUTHORS.AUTHOR_ID = ?");
        q.setParameter(1, id);
        return ((BigDecimal) q.getSingleResult()).longValue();
    }
}
