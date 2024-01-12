package com.mjc.school.repository;

import com.mjc.school.repository.model.BaseEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T extends BaseEntity<K>, K> {

    List<T> readAll(Pageable pageable);

    Optional<T> readById(K id);

    T create(T entity);

    T update(T entity);

    boolean deleteById(K id);
}