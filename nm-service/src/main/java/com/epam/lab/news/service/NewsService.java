package com.epam.lab.news.service;

import java.util.List;

import com.epam.lab.news.domain.News;

public interface NewsService extends GenericService<News, Long> {
    List<News> readNewsByAuthor(Long authorId, int offset, int limit);

    List<News> readNewsByTag(String tag, int offset, int limit);

    List<News> readTopCommentedNews(int newsCount);

    List<News> readAllNewsByPage(int offset, int limit);

    void insertListNews(List<News> newsList);
}
