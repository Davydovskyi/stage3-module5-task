package com.mjc.school.controller;

import com.mjc.school.service.dto.comment.CommentDtoRequest;
import com.mjc.school.service.dto.comment.CommentDtoResponse;

public interface CommentController extends BaseController<CommentDtoRequest, CommentDtoResponse, Long> {
}