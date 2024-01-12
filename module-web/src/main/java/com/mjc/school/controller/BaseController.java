package com.mjc.school.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BaseController<T, R, K> {

    List<R> readAll(Pageable pageable);

    R readById(K id);

    R create(T createRequest);

    R update(Long id, T updateRequest);

    R patch(Long id, JsonPatch patch);

    void deleteById(K id);
}
