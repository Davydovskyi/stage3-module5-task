package com.mjc.school.service.dto.news;

import com.mjc.school.service.validator.annotation.AuthorInfo;
import com.mjc.school.service.validator.annotation.TagsInfo;
import com.mjc.school.service.validator.group.CreateAction;
import lombok.Builder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
public record NewsDtoRequest(
        @NotNull(message = "Title should not be empty")
        @Size(min = 5, max = 30, message = "Title length should be between 5 and 30 characters")
        String title,
        @NotNull(message = "Content should not be empty")
        @Size(min = 5, max = 255, message = "Content length should be between 5 and 300 characters")
        String content,
        @AuthorInfo
        Long authorId,
        @TagsInfo(groups = CreateAction.class)
        List<Long> tagIds) {
}