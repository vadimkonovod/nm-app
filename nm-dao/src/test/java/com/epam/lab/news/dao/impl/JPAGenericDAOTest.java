package com.epam.lab.news.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.epam.lab.news.domain.News;

@RunWith(MockitoJUnitRunner.class)
public class JPAGenericDAOTest {
    @InjectMocks
    JPAGenericDAO<News, Long> dao = new JPAGenericDAO<>(News.class);

    @Mock
    EntityManager em;
    @Mock
    News news;
    @Mock
    List<News> newsList;
    @Mock
    TypedQuery<News> typedQuery;

    Long id = 1L;

    @After
    public void tearDown() throws Exception {
        reset(em, news, newsList, typedQuery);
    }

    @Test
    public void testInsertNews() {
        given(em.merge(eq(news))).willReturn(news);
        assertEquals(news, dao.insert(news));
        verify(em).merge(news);
    }

    @Test
    public void testSelectNews() {
        given(em.find(eq(News.class), eq(id))).willReturn(news);
        assertEquals(news, dao.select(id));
        verify(em).find(News.class, id);
    }
   
   /* @Test
    public void testSelectAllNews() {
        given(em.createQuery(anyString(), eq(News.class))).willReturn(typedQuery);
        given(typedQuery.getResultList()).willReturn(newsList);
        assertEquals(newsList, dao.selectAll());
        verify(em).createQuery(anyString(), eq(News.class));
        verify(typedQuery).getResultList();
    }*/
    
    @Test
    public void testDeleteExistingNews() {
        given(em.find(eq(News.class), eq(id))).willReturn(news);
        doNothing().when(em).remove(news);
        dao.delete(id);
        verify(em).find(News.class, id);
        verify(em).remove(news);
    }

    @Test
    public void testDeleteNotExistingNews() {
        given(em.find(eq(News.class), eq(id))).willReturn(null);
        dao.delete(id);
        verify(em).find(News.class, id);
        verifyNoMoreInteractions(em);
    }
}
