package com.epam.lab.news.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.epam.lab.news.dao.NewsDAO;
import com.epam.lab.news.domain.Author;
import com.epam.lab.news.domain.News;
import com.epam.lab.news.domain.Tag;

@Repository
public class NewsDAOImpl extends JPAGenericDAO<News, Long> implements NewsDAO {
    @Override
    public List<News> selectTopCommentedNews(int newsCount) {
        TypedQuery<News> q = entityManager.createNamedQuery("getNewsOrderedByComments", News.class);
        q.setMaxResults(newsCount);
        return q.getResultList();
    }

    @Override
    public List<News> selectNewsByAuthor(Author author, int offset, int limit) {
        TypedQuery<News> q = entityManager.createNamedQuery("getNewsByAuthor", News.class);
        q.setParameter("author", author);
        pageResult(q, offset, limit);
        return q.getResultList();
    }

    @Override
    public List<News> selectNewsByTag(Tag tag, int offset, int limit) {
        TypedQuery<News> q = entityManager.createNamedQuery("getNewsByTag", News.class);
        q.setParameter("tag", tag);
        pageResult(q, offset, limit);
        return q.getResultList();
    }

    @Override
    public List<News> selectAllNewsByPage(int offset, int limit){
        TypedQuery<News> q = entityManager.createNamedQuery("getAllNews", News.class);
        pageResult(q, offset, limit);
        return q.getResultList();
    }
}
