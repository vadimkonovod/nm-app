package com.epam.lab.news.dao;

import com.epam.lab.news.domain.Tag;

public interface TagDAO extends GenericDAO<Tag, String> {
    Long getTagNewsCount(String name);
}
