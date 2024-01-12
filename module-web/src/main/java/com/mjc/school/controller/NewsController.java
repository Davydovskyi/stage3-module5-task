package com.mjc.school.controller;

import com.mjc.school.service.dto.author.AuthorDtoResponse;
import com.mjc.school.service.dto.comment.CommentDtoResponse;
import com.mjc.school.service.dto.news.NewsDtoRequest;
import com.mjc.school.service.dto.news.NewsDtoResponse;
import com.mjc.school.service.dto.news.NewsQueryParams;
import com.mjc.school.service.dto.tag.TagDtoResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NewsController extends BaseController<NewsDtoRequest, NewsDtoResponse, Long> {

    List<NewsDtoResponse> readAllByFilter(NewsQueryParams filter, Pageable pageable);

    AuthorDtoResponse readAuthorByNewsId(Long id);

    List<TagDtoResponse> readAllTagsByNewsId(Long id);

    List<CommentDtoResponse> readAllCommentsByNewsId(Long id);
}