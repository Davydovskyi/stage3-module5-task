package com.mjc.school.service.dto.news;

import com.mjc.school.service.dto.author.AuthorDtoResponse;
import com.mjc.school.service.dto.tag.TagDtoResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record NewsDtoResponse(Long id,
                              String title,
                              String content,
                              AuthorDtoResponse author,
                              List<TagDtoResponse> tags) {
}