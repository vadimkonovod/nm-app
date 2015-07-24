package com.epam.lab.news.dao;

import java.io.Serializable;
import java.util.List;

/**
 * This interface provides basic CRUD operations with repository.
 * 
 * @author Vadzim_Kanavod
 * 
 * @param <T> entity
 * @param <PK> primary key
 */
public interface GenericDAO<T, PK extends Serializable> {

    /**
     * Inserts new entity to repository
     * 
     * @param entity 
     * @return entity from repository
     */
    T insert(T entity);

    /**
     * 
     * @param id
     * @return entity from repository
     */
    T select(PK id);

    /**
     * 
     * @return list of all entities in repository
     */
    List<T> selectAll();

    /**
     * Updates entity in repository
     * 
     * @param entity
     * @return entity from repository
     */
    T update(T entity);

    /**
     * Deletes from repository
     * 
     * @param id
     */
    void delete(PK id);

    /**
     * Returns the number of entities in repository
     * 
     * @return count of entities
     */
    Long getCount();
}