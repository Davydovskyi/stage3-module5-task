package com.mjc.school.service.dto.tag;

import lombok.Builder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
public record TagDtoRequest(
        @NotNull(message = "Name must not be empty")
        @Size(min = 3, max = 15, message = "Name length must be between 3 and 15")
        String name) {
}