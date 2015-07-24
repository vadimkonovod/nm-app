package com.epam.lab.news.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.epam.lab.news.domain.Author;
import com.epam.lab.news.domain.News;
import com.epam.lab.news.domain.Tag;

@RunWith(MockitoJUnitRunner.class)
public class NewsDAOImplTest {
    @InjectMocks
    NewsDAOImpl dao;

    @Mock
    EntityManager em;
    @Mock
    List<News> newsList;
    @Mock
    TypedQuery<News> typedQuery;
    @Mock
    Author author;
    @Mock
    Tag tag;

    final Long id = 1L;
    int topNewsCount = 5;
    int offset = 0;
    int limit = 10;

    @After
    public void tearDown() throws Exception {
        reset(em, newsList, typedQuery, author, tag);
    }

    @Test
    public void testSelectTopCommentedNews() {
        given(em.createNamedQuery(anyString(), eq(News.class))).willReturn(typedQuery);
        given(typedQuery.getResultList()).willReturn(newsList);
        assertEquals(newsList, dao.selectTopCommentedNews(topNewsCount));
    }

    @Test
    public void testSelectNewsByAuthor() {
        given(em.createNamedQuery(anyString(), eq(News.class))).willReturn(typedQuery);
        given(typedQuery.getResultList()).willReturn(newsList);
        assertEquals(newsList, dao.selectNewsByAuthor(author, offset, limit));
        verify(typedQuery).setParameter("author", author);
    }

    @Test
    public void testSelectNewsByTag() {
        given(em.createNamedQuery(anyString(), eq(News.class))).willReturn(typedQuery);
        given(typedQuery.getResultList()).willReturn(newsList);
        assertEquals(newsList, dao.selectNewsByTag(tag, offset, limit));
        verify(typedQuery).setParameter("tag", tag);
    }
}
