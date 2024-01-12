package com.mjc.school.repository;

import com.mjc.school.repository.model.News;
import com.mjc.school.repository.model.NewsSearchQueryParam;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NewsRepository extends BaseRepository<News, Long> {
    List<News> readAllByFilter(NewsSearchQueryParam filter, Pageable pageable);
}