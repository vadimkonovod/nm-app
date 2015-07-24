package com.epam.lab.news.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.lab.news.dao.CommentDAO;
import com.epam.lab.news.dao.NewsDAO;
import com.epam.lab.news.domain.Comment;
import com.epam.lab.news.service.CommentService;
import com.epam.lab.news.service.exception.EntityNotExistException;

@Service
@Transactional
public class CommentServiceImpl extends JPAGenericService<Comment, Long> implements CommentService {
    @Autowired
    private CommentDAO dao;
    @Autowired
    private NewsDAO newsDAO;

    @Override
    public Comment create(Comment entity) {
        if (newsDAO.select(entity.getNewsId()) == null) {
            throw new EntityNotExistException("News with id=" + entity.getNewsId() + " doesn't exist");
        }
        entity.setCommentId(null);
        return dao.insert(entity);
    }

    @Override
    public Comment read(Long id) {
        Comment comment = dao.select(id);
        if (comment == null) {
            throw new EntityNotExistException("Comment with id=" + id + " doesn't exist");
        }
        return comment;
    }

    @Override
    public Comment update(Comment entity) {
        Comment updatedComment = null;
        if (read(entity.getCommentId()) != null) {
            updatedComment = dao.update(entity);
        } else {
            throw new EntityNotExistException("Comment with id=" + entity.getCommentId() + " doesn't exist");
        }
        return updatedComment;
    }

    @Override
    public void delete(Long id) {
        dao.delete(id);
    }
}
