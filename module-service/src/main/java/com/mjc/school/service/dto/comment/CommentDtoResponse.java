package com.mjc.school.service.dto.comment;

import com.mjc.school.service.dto.news.NewsDtoResponse;
import lombok.Builder;

@Builder
public record CommentDtoResponse(
        Long id,
        String content,
        NewsDtoResponse news) {
}