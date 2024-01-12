package com.mjc.school.repository;

import com.mjc.school.repository.model.Tag;

import java.util.List;

public interface TagRepository extends BaseRepository<Tag, Long> {
    List<Tag> readAllByIds(List<Long> ids);

    List<Tag> readAllByNewsId(Long id);
}