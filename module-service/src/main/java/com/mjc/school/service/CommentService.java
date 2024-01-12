package com.mjc.school.service;

import com.mjc.school.service.dto.comment.CommentDtoRequest;
import com.mjc.school.service.dto.comment.CommentDtoResponse;

import java.util.List;

public interface CommentService extends BaseService<CommentDtoRequest, CommentDtoResponse, Long> {

    List<CommentDtoResponse> readAllByNewsId(Long id);
}