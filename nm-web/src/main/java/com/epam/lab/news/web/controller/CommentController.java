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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epam.lab.news.domain.Comment;
import com.epam.lab.news.service.CommentService;

@RestController
@RequestMapping(value = "/comment")
public class CommentController {
    @Autowired
    private CommentService commentService; 

    @RequestMapping(value = "/{id}", method = GET)
    public Comment viewCommentById(@PathVariable Long id) {
        return commentService.read(id);
    }

    @RequestMapping(method = GET)
    public List<Comment> viewAllComments() {
        return commentService.readAll();
    }

    @RequestMapping(value = "/{newsId}", method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(@PathVariable("newsId") Long newsId, @Valid @RequestBody Comment comment) {
        comment.setNewsId(newsId);
        return commentService.create(comment);
    }

    @RequestMapping(value = "/{id}", method = PUT)
    public Comment updateComment(@PathVariable("id") Long id, @Valid @RequestBody Comment comment) {
        comment.setCommentId(id);
        return commentService.update(comment);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id) {
        commentService.delete(id);
    }
}
