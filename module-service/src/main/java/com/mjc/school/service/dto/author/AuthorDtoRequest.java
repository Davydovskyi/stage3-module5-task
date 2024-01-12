package com.mjc.school.service.dto.author;

import lombok.Builder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
public record AuthorDtoRequest(
        @NotNull(message = "Name should not be empty")
        @Size(min = 3, max = 15, message = "Name length should be between 3 and 15 characters")
        String name) {
}