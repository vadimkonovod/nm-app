package com.epam.lab.news.domain.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

public class HibernateAwareObjectMapper extends ObjectMapper {

    private static final long serialVersionUID = 833809488048711913L;

    public HibernateAwareObjectMapper() {
        registerModule(new Hibernate4Module());
    }
}