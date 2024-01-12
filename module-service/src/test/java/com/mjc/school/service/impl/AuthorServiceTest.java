package com.mjc.school.service.impl;

import com.mjc.school.repository.impl.AuthorRepositoryImpl;
import com.mjc.school.repository.model.Author;
import com.mjc.school.service.dto.author.AuthorDtoRequest;
import com.mjc.school.service.dto.author.AuthorDtoResponse;
import com.mjc.school.service.mapper.AuthorMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
    @Mock
    private AuthorRepositoryImpl authorRepository;
    @Mock
    private AuthorMapper authorMapper;
    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    void readAll() {
        List<Author> authors = List.of(buildAuthor(1L, "author1"), buildAuthor(2L, "author2"));
        Pageable pageable = PageRequest.of(0, 10);
        doReturn(authors).when(authorRepository).readAll(any());
        List<AuthorDtoResponse> expectedResult = List.of(buildAuthorResponse(1L, "author1"), buildAuthorResponse(2L, "author2"));
        doReturn(expectedResult).when(authorMapper).modelListToDtoList(any());

        List<AuthorDtoResponse> actualResult = authorService.readAll(pageable);

        assertThat(actualResult).isEqualTo(expectedResult);
        verify(authorRepository).readAll(pageable);
        verify(authorMapper).modelListToDtoList(authors);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void readById() {
        Author author = buildAuthor(1L, "author1");
        doReturn(Optional.of(author)).when(authorRepository).readById(any());
        AuthorDtoResponse expectedResult = buildAuthorResponse(1L, "author1");
        doReturn(expectedResult).when(authorMapper).modelToDto(any());

        Optional<AuthorDtoResponse> actualResult = authorService.readById(1L);

        assertThat(actualResult).contains(expectedResult);
        verify(authorRepository).readById(1L);
        verify(authorMapper).modelToDto(author);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void readByIdWhenAuthorDoesNotExist() {
        doReturn(Optional.empty()).when(authorRepository).readById(any());

        Optional<AuthorDtoResponse> actualResult = authorService.readById(1L);
        assertThat(actualResult).isEmpty();
        verify(authorRepository).readById(1L);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void readByNewsId() {
        Author author = buildAuthor(1L, "author1");
        doReturn(Optional.of(author)).when(authorRepository).readByNewsId(any());
        AuthorDtoResponse expectedResult = buildAuthorResponse(1L, "author1");
        doReturn(expectedResult).when(authorMapper).modelToDto(any());

        Optional<AuthorDtoResponse> actualResult = authorService.readByNewsId(1L);

        assertThat(actualResult).contains(expectedResult);
        verify(authorRepository).readByNewsId(1L);
        verify(authorMapper).modelToDto(author);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void readByNewsIdWhenAuthorDoesNotExist() {
        doReturn(Optional.empty()).when(authorRepository).readByNewsId(any());

        Optional<AuthorDtoResponse> actualResult = authorService.readByNewsId(1L);
        assertThat(actualResult).isEmpty();
        verify(authorRepository).readByNewsId(1L);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void create() {
        AuthorDtoRequest authorRequest = buildAuthorRequest();
        Author author = buildAuthor(1L, "author1");
        doReturn(author).when(authorMapper).dtoToModel(any());
        doReturn(author).when(authorRepository).create(any());
        AuthorDtoResponse expectedResult = buildAuthorResponse(1L, "author1");
        doReturn(expectedResult).when(authorMapper).modelToDto(any());

        AuthorDtoResponse actualResult = authorService.create(authorRequest);

        assertThat(actualResult).isEqualTo(expectedResult);
        verify(authorMapper).dtoToModel(authorRequest);
        verify(authorRepository).create(author);
        verify(authorMapper).modelToDto(author);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void createWhenAuthorDoesNotSave() {
        AuthorDtoRequest authorRequest = buildAuthorRequest();
        Author author = buildAuthor(1L, "author1");
        doReturn(author).when(authorMapper).dtoToModel(any());
        doReturn(null).when(authorRepository).create(any());

        assertThrowsExactly(NoSuchElementException.class, () -> authorService.create(authorRequest));

        verify(authorRepository).create(author);
        verify(authorMapper).dtoToModel(authorRequest);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void update() {
        AuthorDtoRequest authorRequest = buildAuthorRequest();
        Author author = buildAuthor(1L, "author1");
        doReturn(Optional.of(author)).when(authorRepository).readById(any());
        doReturn(author).when(authorMapper).dtoToModel(any());
        doReturn(author).when(authorRepository).update(any());
        AuthorDtoResponse expectedResult = buildAuthorResponse(1L, "author1");
        doReturn(expectedResult).when(authorMapper).modelToDto(any());

        Optional<AuthorDtoResponse> actualResult = authorService.update(1L, authorRequest);

        assertThat(actualResult).contains(expectedResult);
        verify(authorMapper).dtoToModel(authorRequest);
        verify(authorRepository).readById(1L);
        verify(authorRepository).update(author);
        verify(authorMapper).modelToDto(author);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void updateWhenAuthorDoesNotExist() {
        AuthorDtoRequest authorRequest = buildAuthorRequest();
        doReturn(Optional.empty()).when(authorRepository).readById(any());

        Optional<AuthorDtoResponse> actualResult = authorService.update(1L, authorRequest);
        assertThat(actualResult).isEmpty();
        verify(authorRepository).readById(1L);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void deleteById() {
        Author author = buildAuthor(1L, "author1");
        doReturn(Optional.of(author)).when(authorRepository).readById(any());
        doReturn(true).when(authorRepository).deleteById(any());

        boolean actualResult = authorService.deleteById(1L);

        assertThat(actualResult).isTrue();
        verify(authorRepository).readById(1L);
        verify(authorRepository).deleteById(1L);
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    void deleteByIdWhenAuthorDoesNotExist() {
        doReturn(Optional.empty()).when(authorRepository).readById(any());

        boolean actualResult = authorService.deleteById(1L);
        assertThat(actualResult).isFalse();
        verify(authorRepository).readById(1L);
        verifyNoMoreInteractions(authorRepository);
    }

    private Author buildAuthor(Long id, String name) {
        return Author.builder()
                .id(id)
                .name(name)
                .build();
    }

    private AuthorDtoRequest buildAuthorRequest() {
        return AuthorDtoRequest.builder()
                .name("author1")
                .build();
    }

    private AuthorDtoResponse buildAuthorResponse(Long id, String name) {
        return AuthorDtoResponse.builder()
                .id(id)
                .name(name)
                .build();
    }
}