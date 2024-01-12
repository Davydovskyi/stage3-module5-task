package com.mjc.school.service;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, R, K> {
    List<R> readAll(Pageable pageable);

    Optional<R> readById(K id);

    R create(T createRequest);

    Optional<R> update(K id, T updateRequest);

    boolean deleteById(K id);

    R patch(K id, T patchRequest);
}
