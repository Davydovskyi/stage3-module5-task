package com.mjc.school.repository.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class NewsSearchQueryParam {
    private String title;
    private String content;
    private String authorName;
    @Builder.Default
    private List<Long> tagIds = new ArrayList<>();
    @Builder.Default
    private List<String> tagNames = new ArrayList<>();
}