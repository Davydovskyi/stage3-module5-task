package com.mjc.school.repository;

import com.mjc.school.repository.model.Author;

import java.util.Optional;

public interface AuthorRepository extends BaseRepository<Author, Long> {
    Optional<Author> readByNewsId(Long id);
}