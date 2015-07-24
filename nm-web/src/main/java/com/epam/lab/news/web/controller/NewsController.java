package com.epam.lab.news.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epam.lab.news.domain.News;
import com.epam.lab.news.service.AuthorService;
import com.epam.lab.news.service.NewsService;
import com.epam.lab.news.service.TagService;

@RestController
@RequestMapping(value = "/news")
public class NewsController {

    @Autowired
    private NewsService newsService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private TagService tagService;

    @RequestMapping(value = "/{id}", method = GET)
    public News viewNewsById(@PathVariable Long id) {
        return newsService.read(id);
    }

    @RequestMapping(method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public News addNews(@Valid @RequestBody News news) {
        return newsService.create(news);
    }

    @RequestMapping(value = "/{id}", method = PUT)
    public News updateNews(@PathVariable("id") Long id, @Valid @RequestBody News news) {
        news.setNewsId(id);
        return newsService.update(news);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteNews(@PathVariable Long id) {
        newsService.delete(id);
    }

    @RequestMapping(method = GET)
    public List<News> viewAllNewsByPage(
            @RequestParam(defaultValue = "0", value = "offset") int offset,
            @RequestParam(defaultValue = "6", value = "limit") int limit) {
        return newsService.readAllNewsByPage(offset, limit);
    }

    @RequestMapping(value = "/author", method = GET)
    public List<News> viewNewsByAuthor(
            @RequestParam("id") Long id,
            @RequestParam(defaultValue = "0", value = "offset") int offset,
            @RequestParam(defaultValue = "6", value = "limit") int limit) {
        return newsService.readNewsByAuthor(id, offset, limit);
    }

    @RequestMapping(value = "/tag", method = GET)
    public List<News> viewNewsByTag(
            @RequestParam("tag") String tag,
            @RequestParam(defaultValue = "0", value = "offset") int offset,
            @RequestParam(defaultValue = "6", value = "limit") int limit) {
        return newsService.readNewsByTag(tag, offset, limit);
    }

    @RequestMapping(value = "/top", method = GET)
    public List<News> viewTopCommentedNews(
            @RequestParam(defaultValue = "5", value = "count") int count) {
        return newsService.readTopCommentedNews(count);
    }
    
    @RequestMapping(value = "/count", method = GET)
    public Long getNewsCount() {
        return newsService.getCount();
    }

    @RequestMapping(value = "/count/author", method = GET)
    public Long getAuthorNewsCount(@RequestParam("id") Long id) {
        return authorService.getAuthorNewsCount(id);
    }

    @RequestMapping(value = "/count/tag", method = GET)
    public Long getTagNewsCount(@RequestParam("tag") String name) {
        return tagService.getTagNewsCount(name);
    }
}