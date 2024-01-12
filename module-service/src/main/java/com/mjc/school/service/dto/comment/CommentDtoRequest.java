package com.mjc.school.service.dto.comment;

import com.mjc.school.service.validator.annotation.NewsInfo;
import com.mjc.school.service.validator.group.CreateAction;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
public record CommentDtoRequest(
        @NotNull(message = "Content should not be empty")
        @Size(min = 5, max = 255, message = "Content length should be between 5 and 255 characters")
        String content,
        @NewsInfo(groups = CreateAction.class)
        Long newsId) {
}