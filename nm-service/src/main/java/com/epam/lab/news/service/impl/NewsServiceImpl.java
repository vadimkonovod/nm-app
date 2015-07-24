package com.epam.lab.news.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.lab.news.dao.NewsDAO;
import com.epam.lab.news.domain.Author;
import com.epam.lab.news.domain.News;
import com.epam.lab.news.domain.Tag;
import com.epam.lab.news.service.NewsService;
import com.epam.lab.news.service.exception.EntityNotExistException;

@Service
@Transactional
public class NewsServiceImpl extends JPAGenericService<News, Long> implements NewsService {
    @Autowired
    private NewsDAO dao;

    @Override
    public News create(News entity) {
        entity.setNewsId(null);
        return dao.insert(entity);
    }

    @Override
    public News read(Long id) {
        News news = dao.select(id);
        if (news == null) {
            throw new EntityNotExistException("News with id=" + id + " doesn't exist");
        }
        news.getAuthors().size();
        news.getComments().size();
        news.getTags().size();
        return news;
    }

    @Override
    public News update(News entity) {
        if (entity.getNewsId() == null) {
            throw new EntityNotExistException("Can't update news with ID=null");
        }

        News updatedNews = null;
        if (read(entity.getNewsId()) != null) {
            updatedNews = dao.update(entity);
        } else {
            throw new EntityNotExistException("News with id=" + entity.getNewsId() + " doesn't exist");
        }
        return updatedNews;
    }

    @Override
    public void delete(Long id) {
        dao.delete(id);
    }

    @Override
    public List<News> readNewsByAuthor(Long authorId, int offset, int limit) {
        Author author = new Author();
        author.setAuthorId(authorId);
        return dao.selectNewsByAuthor(author, offset, limit);
    }

    @Override
    public List<News> readNewsByTag(String tagName, int offset, int limit) {
        Tag tag = new Tag(tagName);
        return dao.selectNewsByTag(tag, offset, limit);
    }

    @Override
    public List<News> readTopCommentedNews(int newsCount) {
        return dao.selectTopCommentedNews(newsCount);
    }

    @Override
    public List<News> readAllNewsByPage(int offset, int limit) {
        return dao.selectAllNewsByPage(offset, limit);
    }
    
    @Override
    @Transactional
    public void insertListNews(List<News> newsList) {
        for (News news : newsList) {
            news.setNewsId(null);
            create(news);
        }
    }
}
