package com.mjc.school.service.impl;

import com.mjc.school.repository.impl.NewsRepositoryImpl;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.model.NewsSearchQueryParam;
import com.mjc.school.service.dto.author.AuthorDtoResponse;
import com.mjc.school.service.dto.news.NewsDtoRequest;
import com.mjc.school.service.dto.news.NewsDtoResponse;
import com.mjc.school.service.dto.news.NewsQueryParams;
import com.mjc.school.service.mapper.NewsFilterMapper;
import com.mjc.school.service.mapper.NewsMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {
    @Mock
    private NewsRepositoryImpl newsRepository;
    @Mock
    private NewsMapper newsMapper;
    @Mock
    private NewsFilterMapper newsFilterMapper;
    @InjectMocks
    private NewsServiceImpl newsService;

    @Test
    void readAll() {
        List<News> news = List.of(buildNews(1L, "title1"), buildNews(2L, "title2"));
        Pageable pageable = PageRequest.of(0, 10);
        doReturn(news).when(newsRepository).readAll(any());
        List<NewsDtoResponse> expectedResult = List.of(buildNewsResponse(1L, "title1"), buildNewsResponse(2L, "title2"));
        doReturn(expectedResult).when(newsMapper).modelListToDtoList(any());

        List<NewsDtoResponse> actualResult = newsService.readAll(pageable);

        assertThat(actualResult).isEqualTo(expectedResult);
        verify(newsRepository).readAll(pageable);
        verify(newsMapper).modelListToDtoList(news);
        verifyNoMoreInteractions(newsRepository, newsMapper);
    }

    @Test
    void readById() {
        News news = buildNews(1L, "title1");
        doReturn(Optional.of(news)).when(newsRepository).readById(any());
        NewsDtoResponse expectedResult = buildNewsResponse(1L, "title1");
        doReturn(expectedResult).when(newsMapper).modelToDto(any());

        Optional<NewsDtoResponse> actualResult = newsService.readById(1L);

        assertThat(actualResult).contains(expectedResult);
        verify(newsRepository).readById(1L);
        verify(newsMapper).modelToDto(news);
        verifyNoMoreInteractions(newsRepository, newsMapper);
    }

    @Test
    void readByIdWhenNewsDoesNotExist() {
        doReturn(Optional.empty()).when(newsRepository).readById(any());

        Optional<NewsDtoResponse> actualResult = newsService.readById(1L);
        assertThat(actualResult).isEmpty();
        verify(newsRepository).readById(1L);
        verifyNoMoreInteractions(newsRepository, newsMapper);
    }

    @Test
    void create() {
        NewsDtoRequest newsRequest = buildNewsRequest();
        News news = buildNews(1L, "title1");
        doReturn(news).when(newsMapper).dtoToModel(any());
        doReturn(news).when(newsRepository).create(any());
        NewsDtoResponse expectedResult = buildNewsResponse(1L, "title1");
        doReturn(expectedResult).when(newsMapper).modelToDto(any());

        NewsDtoResponse actualResult = newsService.create(newsRequest);

        assertThat(actualResult).isEqualTo(expectedResult);
        verify(newsMapper).dtoToModel(newsRequest);
        verify(newsRepository).create(news);
        verify(newsMapper).modelToDto(news);
        verifyNoMoreInteractions(newsRepository, newsMapper);
    }

    @Test
    void createWhenNewsDoesNotSave() {
        NewsDtoRequest newsRequest = buildNewsRequest();
        News news = buildNews(1L, "title1");
        doReturn(news).when(newsMapper).dtoToModel(any());
        doReturn(null).when(newsRepository).create(any());

        assertThrowsExactly(NoSuchElementException.class, () -> newsService.create(newsRequest));
        verify(newsMapper).dtoToModel(newsRequest);
        verify(newsRepository).create(news);
        verifyNoMoreInteractions(newsRepository, newsMapper);
    }

    @Test
    void update() {
        NewsDtoRequest newsRequest = buildNewsRequest();
        News news = buildNews(1L, "title1");
        doReturn(Optional.of(news)).when(newsRepository).readById(any());
        doReturn(news).when(newsMapper).dtoToModel(any());
        doReturn(news).when(newsRepository).update(any());
        NewsDtoResponse expectedResult = buildNewsResponse(1L, "title1");
        doReturn(expectedResult).when(newsMapper).modelToDto(any());

        Optional<NewsDtoResponse> actualResult = newsService.update(1L, newsRequest);

        assertThat(actualResult).contains(expectedResult);
        verify(newsRepository).readById(1L);
        verify(newsMapper).dtoToModel(newsRequest);
        verify(newsRepository).update(news);
        verify(newsMapper).modelToDto(news);
        verifyNoMoreInteractions(newsRepository, newsMapper);
    }

    @Test
    void updateWhenNewsDoesNotExist() {
        NewsDtoRequest newsRequest = buildNewsRequest();
        doReturn(Optional.empty()).when(newsRepository).readById(any());

        Optional<NewsDtoResponse> actualResult = newsService.update(1L, newsRequest);
        assertThat(actualResult).isEmpty();
        verify(newsRepository).readById(1L);
        verifyNoMoreInteractions(newsRepository, newsMapper);
    }

    @Test
    void deleteById() {
        News news = buildNews(1L, "title1");
        doReturn(Optional.of(news)).when(newsRepository).readById(any());
        doReturn(true).when(newsRepository).deleteById(any());

        boolean actualResult = newsService.deleteById(1L);

        assertThat(actualResult).isTrue();
        verify(newsRepository).readById(1L);
        verify(newsRepository).deleteById(1L);
        verifyNoMoreInteractions(newsRepository);
    }

    @Test
    void deleteByIdWhenNewsDoesNotExist() {
        doReturn(Optional.empty()).when(newsRepository).readById(any());

        boolean actualResult = newsService.deleteById(1L);
        assertThat(actualResult).isFalse();
        verify(newsRepository).readById(1L);
        verifyNoMoreInteractions(newsRepository);
    }

    @Test
    void readAllByFilter() {
        NewsQueryParams newsQueryParams = buildNewsDtoFilter();
        NewsSearchQueryParam newsSearchQueryParam = buildNewsFilter();
        doReturn(newsSearchQueryParam).when(newsFilterMapper).dtoToModel(any());
        List<News> news = List.of(buildNews(1L, "title1"), buildNews(2L, "title2"));
        Pageable pageable = PageRequest.of(0, 2);
        doReturn(news).when(newsRepository).readAllByFilter(any(), any());
        List<NewsDtoResponse> expectedResult = List.of(buildNewsResponse(1L, "title1"), buildNewsResponse(2L, "title2"));
        doReturn(expectedResult).when(newsMapper).fullModelListToDtoList(any());

        List<NewsDtoResponse> actualResult = newsService.readAllByFilter(newsQueryParams, pageable);

        assertThat(actualResult).isEqualTo(expectedResult);
        verify(newsFilterMapper).dtoToModel(newsQueryParams);
        verify(newsRepository).readAllByFilter(newsSearchQueryParam, pageable);
        verify(newsMapper).fullModelListToDtoList(news);
        verifyNoMoreInteractions(newsRepository, newsFilterMapper, newsMapper);
    }

    @Test
    void readAllByFilterWhenNewsDoesNotExist() {
        NewsQueryParams newsQueryParams = buildNewsDtoFilter();
        NewsSearchQueryParam newsSearchQueryParam = buildNewsFilter();
        doReturn(newsSearchQueryParam).when(newsFilterMapper).dtoToModel(any());
        Pageable pageable = PageRequest.of(0, 2);
        doReturn(Collections.emptyList()).when(newsRepository).readAllByFilter(any(), any());

        List<NewsDtoResponse> actualResult = newsService.readAllByFilter(newsQueryParams, pageable);

        assertThat(actualResult).isEmpty();
        verify(newsFilterMapper).dtoToModel(newsQueryParams);
        verify(newsRepository).readAllByFilter(newsSearchQueryParam, pageable);
        verifyNoMoreInteractions(newsRepository, newsFilterMapper);
    }

    private NewsDtoResponse buildNewsResponse(Long id, String title) {
        return NewsDtoResponse.builder()
                .id(id)
                .title(title)
                .author(AuthorDtoResponse.builder().id(1L).build())
                .content("content")
                .build();
    }

    private NewsDtoRequest buildNewsRequest() {
        return NewsDtoRequest.builder()
                .title("title1")
                .authorId(1L)
                .content("content")
                .build();
    }

    private News buildNews(Long id, String title) {
        return News.builder()
                .id(id)
                .title(title)
                .author(Author.builder().id(1L).build())
                .content("content")
                .build();
    }

    private NewsQueryParams buildNewsDtoFilter() {
        return NewsQueryParams.builder()
                .authorName("author1")
                .content("content")
                .title("title")
                .build();
    }

    private NewsSearchQueryParam buildNewsFilter() {
        return NewsSearchQueryParam.builder()
                .authorName("author1")
                .content("content")
                .title("title")
                .build();
    }
}