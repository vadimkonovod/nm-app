package com.epam.lab.news.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.lab.news.dao.TagDAO;
import com.epam.lab.news.domain.Tag;
import com.epam.lab.news.service.TagService;
import com.epam.lab.news.service.exception.EntityNotExistException;
import com.epam.lab.news.service.exception.ServiceException;

@Service
@Transactional
public class TagServiceImpl extends JPAGenericService<Tag, String> implements TagService {
    @Autowired
    private TagDAO dao;
    
    @Override
    public Tag create(Tag entity) {
        return dao.insert(entity);
    }

    @Override
    public Tag read(String id) {
        Tag tag = dao.select(id);
        if (tag == null) {
            throw new EntityNotExistException("Tag \"" + id + "\" doesn't exist");
        }
        return tag;
    }

    @Override
    public List<Tag> readAll() {
        List<Tag> tags = dao.selectAll();
        return tags;
    }

    @Override
    public Tag update(Tag entity) {
        throw new ServiceException("Operation is not available");
    }

    @Override
    public void delete(String id) {
        dao.delete(id);
    }

    @Override
    public Long getTagNewsCount(String name) {
        return dao.getTagNewsCount(name);
    }
}
