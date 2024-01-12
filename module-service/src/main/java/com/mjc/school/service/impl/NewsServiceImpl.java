package com.mjc.school.service.impl;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.model.News;
import com.mjc.school.service.NewsService;
import com.mjc.school.service.dto.news.NewsDtoRequest;
import com.mjc.school.service.dto.news.NewsDtoResponse;
import com.mjc.school.service.dto.news.NewsQueryParams;
import com.mjc.school.service.mapper.NewsFilterMapper;
import com.mjc.school.service.mapper.NewsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;
    private final NewsFilterMapper newsFilterMapper;

    @Override
    public List<NewsDtoResponse> readAll(Pageable pageable) {
        return newsMapper.modelListToDtoList(newsRepository.readAll(pageable));
    }

    @Override
    public Optional<NewsDtoResponse> readById(Long id) {
        return newsRepository.readById(id)
                .map(newsMapper::modelToDto);
    }

    @Override
    @Transactional
    public NewsDtoResponse create(NewsDtoRequest dtoRequest) {
        return Optional.of(dtoRequest)
                .map(newsMapper::dtoToModel)
                .map(newsRepository::create)
                .map(newsMapper::modelToDto)
                .orElseThrow();
    }

    @Override
    @Transactional
    public Optional<NewsDtoResponse> update(Long id, NewsDtoRequest dtoRequest) {
        return newsRepository.readById(id)
                .map(model -> newsMapper.dtoToModel(dtoRequest))
                .map(news -> {
                    news.setId(id);
                    return newsRepository.update(news);
                })
                .map(newsMapper::modelToDto);
    }

    @Transactional
    @Override
    public NewsDtoResponse patch(Long id, NewsDtoRequest dtoRequest) {
        News news = newsMapper.dtoToModel(dtoRequest);
        news.setId(id);
        return newsMapper.modelToDto(newsRepository.update(news));
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return newsRepository.readById(id).
                map(newsModel -> newsRepository.deleteById(id))
                .orElse(false);
    }

    @Override
    public List<NewsDtoResponse> readAllByFilter(NewsQueryParams filter, Pageable pageable) {
        return Optional.ofNullable(filter)
                .map(newsFilterMapper::dtoToModel)
                .map(it -> newsRepository.readAllByFilter(it, pageable))
                .map(newsMapper::fullModelListToDtoList)
                .orElse(List.of());
    }
}