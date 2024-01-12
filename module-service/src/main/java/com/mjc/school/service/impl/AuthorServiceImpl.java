package com.mjc.school.service.impl;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.model.Author;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.dto.author.AuthorDtoRequest;
import com.mjc.school.service.dto.author.AuthorDtoResponse;
import com.mjc.school.service.mapper.AuthorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    public List<AuthorDtoResponse> readAll(Pageable pageable) {
        return authorMapper.modelListToDtoList(authorRepository.readAll(pageable));
    }

    @Override
    public Optional<AuthorDtoResponse> readById(Long id) {
        return authorRepository.readById(id)
                .map(authorMapper::modelToDto);
    }

    @Override
    public Optional<AuthorDtoResponse> readByNewsId(Long id) {
        return authorRepository.readByNewsId(id)
                .map(authorMapper::modelToDto);
    }

    @Override
    @Transactional
    public AuthorDtoResponse create(AuthorDtoRequest createRequest) {
        return Optional.of(createRequest)
                .map(authorMapper::dtoToModel)
                .map(authorRepository::create)
                .map(authorMapper::modelToDto)
                .orElseThrow();
    }

    @Override
    @Transactional
    public Optional<AuthorDtoResponse> update(Long id, AuthorDtoRequest updateRequest) {
        return authorRepository.readById(id)
                .map(model -> authorMapper.dtoToModel(updateRequest))
                .map(author -> {
                    author.setId(id);
                    return authorRepository.update(author);
                })
                .map(authorMapper::modelToDto);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return authorRepository.readById(id)
                .map(model -> authorRepository.deleteById(id))
                .orElse(false);
    }

    @Transactional
    @Override
    public AuthorDtoResponse patch(Long id, AuthorDtoRequest patchRequest) {
        Author author = authorMapper.dtoToModel(patchRequest);
        author.setId(id);
        return authorMapper.modelToDto(authorRepository.update(author));
    }
}