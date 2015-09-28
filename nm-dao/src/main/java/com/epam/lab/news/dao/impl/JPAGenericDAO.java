package com.epam.lab.news.dao.impl;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import com.epam.lab.news.domain.News;
import org.springframework.core.GenericTypeResolver;

import com.epam.lab.news.dao.GenericDAO;

public class JPAGenericDAO<T, PK extends Serializable> implements GenericDAO<T, PK> {
    private final Class<T> genericType;

    @PersistenceContext(name = "entityManager")
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public JPAGenericDAO() {
        this.genericType = (Class<T>) GenericTypeResolver.resolveTypeArguments(getClass(), JPAGenericDAO.class)[0];
    }

    public JPAGenericDAO(Class<T> genericType) {
        this.genericType = genericType;
    }

    @Override
    public T insert(T entity) {
        return entityManager.merge(entity);
    }

    @Override
    public T select(PK id) {
        return entityManager.find(genericType, id);
    }

    @Override
    public List<T> selectAll() {
        TypedQuery<T> q = entityManager.createNamedQuery("getAll" + genericType.getSimpleName(), genericType);
        return q.getResultList();
    }

    @Override
    public T update(T entity) {
        return entityManager.merge(entity);
    }

    @Override
    public void delete(PK id) {
        T entity = entityManager.find(genericType, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public Long getCount() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(genericType)));
        return entityManager.createQuery(cq).getSingleResult();
    }

    protected void pageResult(TypedQuery<T> q, int offset, int limit) {
        q.setFirstResult(offset);
        q.setMaxResults(limit);
    }
}
