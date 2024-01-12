package com.mjc.school.service;

import com.mjc.school.service.dto.news.NewsDtoRequest;
import com.mjc.school.service.dto.news.NewsDtoResponse;
import com.mjc.school.service.dto.news.NewsQueryParams;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NewsService extends BaseService<NewsDtoRequest, NewsDtoResponse, Long> {

    List<NewsDtoResponse> readAllByFilter(NewsQueryParams filter, Pageable pageable);
}