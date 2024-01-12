package com.mjc.school.service.impl;

import com.mjc.school.repository.impl.TagRepositoryImpl;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.dto.tag.TagDtoRequest;
import com.mjc.school.service.dto.tag.TagDtoResponse;
import com.mjc.school.service.mapper.TagMapper;
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
class TagServiceTest {
    @Mock
    private TagRepositoryImpl tagRepository;
    @Mock
    private TagMapper tagMapper;
    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    void readAll() {
        List<Tag> tags = List.of(buildTag(1L, "tag1"), buildTag(2L, "tag2"));
        Pageable pageable = PageRequest.of(0, 10);
        doReturn(tags).when(tagRepository).readAll(any());
        List<TagDtoResponse> expectedResult = List.of(buildTagDtoResponse(1L, "tag1"), buildTagDtoResponse(2L, "tag2"));
        doReturn(expectedResult).when(tagMapper).modelListToDtoList(any());

        List<TagDtoResponse> actualResult = tagService.readAll(pageable);

        assertThat(actualResult).isEqualTo(expectedResult);
        verify(tagRepository).readAll(pageable);
        verify(tagMapper).modelListToDtoList(tags);
        verifyNoMoreInteractions(tagRepository, tagMapper);
    }

    @Test
    void readById() {
        Tag tag = buildTag(1L, "tag1");
        doReturn(Optional.of(tag)).when(tagRepository).readById(any());
        TagDtoResponse expectedResult = buildTagDtoResponse(1L, "tag1");
        doReturn(expectedResult).when(tagMapper).modelToDto(any());

        Optional<TagDtoResponse> actualResult = tagService.readById(1L);

        assertThat(actualResult).contains(expectedResult);
        verify(tagRepository).readById(1L);
        verify(tagMapper).modelToDto(tag);
        verifyNoMoreInteractions(tagRepository, tagMapper);
    }

    @Test
    void readByIdWhenTagNotFound() {
        doReturn(Optional.empty()).when(tagRepository).readById(any());

        Optional<TagDtoResponse> actualResult = tagService.readById(1L);
        assertThat(actualResult).isEmpty();
        verify(tagRepository).readById(1L);
        verifyNoMoreInteractions(tagRepository, tagMapper);
    }

    @Test
    void readAllByNewsId() {
        List<Tag> tags = List.of(buildTag(1L, "tag1"), buildTag(2L, "tag2"));
        doReturn(tags).when(tagRepository).readAllByNewsId(any());
        List<TagDtoResponse> expectedResult = List.of(buildTagDtoResponse(1L, "tag1"), buildTagDtoResponse(2L, "tag2"));
        doReturn(expectedResult).when(tagMapper).modelListToDtoList(any());

        List<TagDtoResponse> actualResult = tagService.readAllByNewsId(1L);

        assertThat(actualResult).isEqualTo(expectedResult);
        verify(tagRepository).readAllByNewsId(1L);
        verify(tagMapper).modelListToDtoList(tags);
        verifyNoMoreInteractions(tagRepository, tagMapper);
    }

    @Test
    void create() {
        TagDtoRequest tagDtoRequest = buildTagDtoRequest();
        Tag tag = buildTag(1L, "tag1");
        doReturn(tag).when(tagMapper).dtoToModel(any());
        doReturn(tag).when(tagRepository).create(any());
        TagDtoResponse expectedResult = buildTagDtoResponse(1L, "tag1");
        doReturn(expectedResult).when(tagMapper).modelToDto(any());

        TagDtoResponse actualResult = tagService.create(tagDtoRequest);

        assertThat(actualResult).isEqualTo(expectedResult);
        verify(tagMapper).dtoToModel(tagDtoRequest);
        verify(tagRepository).create(tag);
        verify(tagMapper).modelToDto(tag);
        verifyNoMoreInteractions(tagRepository, tagMapper);
    }

    @Test
    void createWhenTagDoesNotSave() {
        TagDtoRequest tagDtoRequest = buildTagDtoRequest();
        Tag tag = buildTag(1L, "tag1");
        doReturn(tag).when(tagMapper).dtoToModel(any());
        doReturn(null).when(tagRepository).create(any());

        assertThrowsExactly(NoSuchElementException.class, () -> tagService.create(tagDtoRequest));
        verify(tagMapper).dtoToModel(tagDtoRequest);
        verify(tagRepository).create(tag);
        verifyNoMoreInteractions(tagRepository, tagMapper);
    }

    @Test
    void update() {
        TagDtoRequest tagDtoRequest = buildTagDtoRequest();
        Tag tag = buildTag(1L, "tag1");
        doReturn(Optional.of(tag)).when(tagRepository).readById(any());
        doReturn(tag).when(tagMapper).dtoToModel(any());
        doReturn(tag).when(tagRepository).update(any());
        TagDtoResponse expectedResult = buildTagDtoResponse(1L, "tag1");
        doReturn(expectedResult).when(tagMapper).modelToDto(any());

        Optional<TagDtoResponse> actualResult = tagService.update(1L, tagDtoRequest);

        assertThat(actualResult).contains(expectedResult);
        verify(tagMapper).dtoToModel(tagDtoRequest);
        verify(tagRepository).readById(1L);
        verify(tagRepository).update(tag);
        verify(tagMapper).modelToDto(tag);
        verifyNoMoreInteractions(tagRepository, tagMapper);
    }

    @Test
    void updateWhenTagDoesNotExist() {
        TagDtoRequest tagDtoRequest = buildTagDtoRequest();
        doReturn(Optional.empty()).when(tagRepository).readById(any());

        Optional<TagDtoResponse> actualResult = tagService.update(1L, tagDtoRequest);
        assertThat(actualResult).isEmpty();
        verify(tagRepository).readById(1L);
        verifyNoMoreInteractions(tagRepository, tagMapper);
    }

    @Test
    void deleteById() {
        Tag tag = buildTag(1L, "tag1");
        doReturn(Optional.of(tag)).when(tagRepository).readById(any());
        doReturn(true).when(tagRepository).deleteById(any());

        boolean actualResult = tagService.deleteById(1L);

        assertThat(actualResult).isTrue();
        verify(tagRepository).readById(1L);
        verify(tagRepository).deleteById(1L);
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    void deleteByIdWhenTagDoesNotExist() {
        doReturn(Optional.empty()).when(tagRepository).readById(any());

        boolean actualResult = tagService.deleteById(1L);
        assertThat(actualResult).isFalse();
        verify(tagRepository).readById(1L);
        verifyNoMoreInteractions(tagRepository);
    }

    private TagDtoRequest buildTagDtoRequest() {
        return TagDtoRequest.builder()
                .name("tag1")
                .build();
    }

    private Tag buildTag(Long id, String name) {
        return Tag.builder()
                .id(id)
                .name(name)
                .build();
    }

    private TagDtoResponse buildTagDtoResponse(Long id, String name) {
        return TagDtoResponse.builder()
                .id(id)
                .name(name)
                .build();
    }
}