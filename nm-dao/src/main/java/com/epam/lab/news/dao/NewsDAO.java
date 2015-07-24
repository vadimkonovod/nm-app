package com.epam.lab.news.dao;

import java.util.List;

import com.epam.lab.news.domain.Author;
import com.epam.lab.news.domain.News;
import com.epam.lab.news.domain.Tag;

public interface NewsDAO extends GenericDAO<News, Long> {
    List<News> selectTopCommentedNews(int newsCount);

    List<News> selectNewsByAuthor(Author author, int offset, int limit);

    List<News> selectNewsByTag(Tag tag, int offset, int limit);

    List<News> selectAllNewsByPage(int offset, int limit);
}
