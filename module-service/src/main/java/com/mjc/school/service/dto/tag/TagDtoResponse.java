package com.mjc.school.service.dto.tag;

import lombok.Builder;

@Builder
public record TagDtoResponse(Long id,
                             String name) {
}