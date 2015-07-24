package com.epam.lab.news.service;

import java.io.Serializable;
import java.util.List;

/**
 * This service interface provides basic CRUD operations.
 * 
 * @author Vadzim_Kanavod
 * 
 * @param <T> entity
 * @param <PK> primary key
 */
public interface GenericService<T, PK extends Serializable> {

    /**
     * Adds entity to repository
     * 
     * @param entity that will be added
     * @return entity with filled id field
     */
    T create(T entity);

    /**
     * Retrieves entity from repository
     * 
     * @param id
     * @return entity by id
     * @throws EntityNotExistException if entity with this id doesn't exist
     */
    T read(PK id);

    /**
     * Retrieves all entities from repository
     * 
     * @return list of all entities in repository
     */
    List<T> readAll();

    /**
     * Updates entity
     * 
     * @param entity to be updated
     * @return updated entity
     * @throws EntityNotExistException if entity with this id doesn't exist
     *         or cannot be updated
     */
    T update(T entity);
    
    /**
     * Removes the entity with specified id from repository
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
