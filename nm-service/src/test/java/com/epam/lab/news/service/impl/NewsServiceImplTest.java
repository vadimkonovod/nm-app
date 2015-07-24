package com.epam.lab.news.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.epam.lab.news.dao.NewsDAO;
import com.epam.lab.news.domain.Author;
import com.epam.lab.news.domain.Comment;
import com.epam.lab.news.domain.News;
import com.epam.lab.news.domain.Tag;
import com.epam.lab.news.service.exception.ServiceException;

@RunWith(MockitoJUnitRunner.class)
public class NewsServiceImplTest {
    @InjectMocks
    NewsServiceImpl service;

    @Mock
    NewsDAO dao;
    @Mock
    News news;
    @Mock
    List<News> newsList;
    @Mock
    Set<Author> authors;
    @Mock
    List<Comment> comments;
    @Mock
    Set<Tag> tags;

    Long id = 1L;
    String tagName = "tagName";
    int topNewsCount = 5;
    int offset = 0;
    int limit = 10;

    @After
    public void tearDown() throws Exception {
        reset(news, dao, newsList, authors, tags, comments);
    }

    @Test
    public void testCreateNews() {
        given(dao.insert(news)).willReturn(news);
        assertEquals(news, service.create(news));
    }

    @Test
    public void testReadNews() {
        given(dao.select(id)).willReturn(news);
        given(news.getAuthors()).willReturn(authors);
        given(news.getComments()).willReturn(comments);
        given(news.getTags()).willReturn(tags);
        assertEquals(news, service.read(id));
        verify(dao).select(id);
        verify(news).getAuthors();
        verify(news).getComments();
        verify(news).getTags();
        verify(authors).size();
        verify(comments).size();
        verify(tags).size();
    }
    
    @Test(expected = ServiceException.class)
    public void testReadNotExistingNews() {
        given(dao.select(id)).willReturn(null);
        service.read(id);
    }

    @Test
    public void testUpdateNews() {
        given(news.getNewsId()).willReturn(id);
        given(dao.select(id)).willReturn(news);
        given(dao.update(news)).willReturn(news);
        assertEquals(news, service.update(news));
        verify(news, times(2)).getNewsId();
        verify(dao, times(1)).select(id);
        verify(dao, times(1)).update(news);
    }

    @Test(expected = ServiceException.class)
    public void testUpdateNewsWithNullId() {
        given(news.getNewsId()).willReturn(null);
        service.update(news);
    }
    
    @Test(expected = ServiceException.class)
    public void testUpdateNotExistingNews() {
        given(news.getNewsId()).willReturn(id);
        given(dao.select(id)).willReturn(null);
        service.update(news);
    }

    @Test
    public void testDeleteNews() {
        service.delete(id);
        verify(dao).delete(id);
    }

    @Test
    public void testReadNewsByAuthor() {
        Author author = new Author();
        author.setAuthorId(id);
        when(dao.selectNewsByAuthor(author, offset, limit)).thenReturn(newsList);
        assertEquals(newsList, service.readNewsByAuthor(id, offset, limit));
        verify(dao).selectNewsByAuthor(author, offset, limit);
    }

    @Test
    public void testReadNewsByTag() {
        Tag tag = new Tag();
        tag.setTag(tagName);
        given(dao.selectNewsByTag(tag, offset, limit)).willReturn(newsList);
        assertEquals(newsList, service.readNewsByTag(tagName, offset, limit));
        verify(dao).selectNewsByTag(tag, offset, limit);
    }

    @Test
    public void testReadTopCommentedNews() {
        given(dao.selectTopCommentedNews(topNewsCount)).willReturn(newsList);
        assertEquals(newsList, service.readTopCommentedNews(topNewsCount));
    }

    @Test
    public void testReadAllNews() {
        given(dao.selectAll()).willReturn(newsList);
        assertEquals(newsList, service.readAll());
        verify(dao).selectAll();
    }

}
