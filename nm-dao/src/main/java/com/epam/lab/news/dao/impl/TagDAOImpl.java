package com.epam.lab.news.dao.impl;

import java.math.BigDecimal;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.epam.lab.news.dao.TagDAO;
import com.epam.lab.news.domain.Tag;

@Repository
public class TagDAOImpl extends JPAGenericDAO<Tag, String> implements TagDAO {

    @Override
    public Long getTagNewsCount(String name) {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM NEWS_TAG WHERE NEWS_TAG.TAG = ?");
        q.setParameter(1, name);
        return ((BigDecimal) q.getSingleResult()).longValue();
    }
}
