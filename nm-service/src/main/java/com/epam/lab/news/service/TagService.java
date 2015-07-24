package com.epam.lab.news.service;

import com.epam.lab.news.domain.Tag;

public interface TagService extends GenericService<Tag, String> {
    Long getTagNewsCount(String name);
}
