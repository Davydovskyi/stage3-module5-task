package com.mjc.school.service.mapper;

import com.mjc.school.repository.model.NewsSearchQueryParam;
import com.mjc.school.service.dto.news.NewsQueryParams;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NewsFilterMapper {

    NewsSearchQueryParam dtoToModel(NewsQueryParams dtoFilter);
}