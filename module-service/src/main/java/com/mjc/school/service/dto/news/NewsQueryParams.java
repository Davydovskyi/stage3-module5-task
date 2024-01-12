package com.mjc.school.service.dto.news;

import lombok.Builder;

import java.util.List;

@Builder
public record NewsQueryParams(String title,
                              String content,
                              String authorName,
                              List<Long> tagIds,
                              List<String> tagNames) {
}