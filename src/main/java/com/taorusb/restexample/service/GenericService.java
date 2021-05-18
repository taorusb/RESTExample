package com.taorusb.restexample.service;

import java.util.List;

public interface GenericService<T, ID extends Number> {

    T getById(ID id);

    T update(T entity);

    T save(T entity);

    void delete(ID id);

    List<T> getAll();

}
