package com.epam.lab.news.dao.impl;

import org.springframework.stereotype.Repository;

import com.epam.lab.news.dao.CommentDAO;
import com.epam.lab.news.domain.Comment;

@Repository
public class CommentDAOImpl extends JPAGenericDAO<Comment, Long> implements CommentDAO {
}
