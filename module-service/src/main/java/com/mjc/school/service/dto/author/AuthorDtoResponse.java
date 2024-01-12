package com.mjc.school.service.dto.author;

import lombok.Builder;

@Builder
public record AuthorDtoResponse(Long id,
                                String name) {
}