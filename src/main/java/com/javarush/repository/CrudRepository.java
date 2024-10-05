package com.javarush.repository;

import java.util.List;

public interface CrudRepository <T, I>{
    T getById(I id);

    T save(T entity);

    void delete(I id);

    List<T> getAll();

    List<T> getItems(int offset, int limit);

    int getTotalCount();
}
