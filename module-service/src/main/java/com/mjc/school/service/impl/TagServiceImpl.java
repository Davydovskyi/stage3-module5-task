package com.mjc.school.service.impl;

import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.tag.TagDtoRequest;
import com.mjc.school.service.dto.tag.TagDtoResponse;
import com.mjc.school.service.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public List<TagDtoResponse> readAll(Pageable pageable) {
        return tagMapper.modelListToDtoList(tagRepository.readAll(pageable));
    }

    @Override
    public List<TagDtoResponse> readAllByNewsId(Long id) {
        return tagMapper.modelListToDtoList(tagRepository.readAllByNewsId(id));
    }

    @Override
    public Optional<TagDtoResponse> readById(Long id) {
        return tagRepository.readById(id)
                .map(tagMapper::modelToDto);
    }

    @Override
    @Transactional
    public TagDtoResponse create(TagDtoRequest createRequest) {
        return Optional.of(createRequest)
                .map(tagMapper::dtoToModel)
                .map(tagRepository::create)
                .map(tagMapper::modelToDto)
                .orElseThrow();
    }

    @Override
    @Transactional
    public Optional<TagDtoResponse> update(Long id, TagDtoRequest updateRequest) {
        return tagRepository.readById(id)
                .map(model -> tagMapper.dtoToModel(updateRequest))
                .map(tag -> {
                    tag.setId(id);
                    return tagRepository.update(tag);
                })
                .map(tagMapper::modelToDto);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return tagRepository.readById(id)
                .map(model -> tagRepository.deleteById(id))
                .orElse(false);
    }

    @Override
    @Transactional
    public TagDtoResponse patch(Long id, TagDtoRequest patchRequest) {
        Tag tag = tagMapper.dtoToModel(patchRequest);
        tag.setId(id);
        return tagMapper.modelToDto(tagRepository.update(tag));
    }
}