
package com.laboratorio.service;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface CrudService<T> {
    List<T> getAll();
    T get(T entity);
    void save(T entity);
    void delete(T entity);
}
