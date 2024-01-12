package com.mjc.school.repository;

import com.mjc.school.repository.model.Comment;

import java.util.List;

public interface CommentRepository extends BaseRepository<Comment, Long> {

    List<Comment> readAllByNewsId(Long id);
}