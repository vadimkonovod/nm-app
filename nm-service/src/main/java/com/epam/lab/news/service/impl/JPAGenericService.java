package com.epam.lab.news.service.impl;

import java.io.Serializable;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import com.epam.lab.news.dao.GenericDAO;
import com.epam.lab.news.service.GenericService;

@Transactional
public abstract class JPAGenericService<T, PK extends Serializable> implements GenericService<T, PK> {
    @Autowired
    GenericDAO<T, PK> dao;

    @Override
    public List<T> readAll() {
        return dao.selectAll();
    }

    @Override
    public Long getCount() {
        return dao.getCount();
    }
}
