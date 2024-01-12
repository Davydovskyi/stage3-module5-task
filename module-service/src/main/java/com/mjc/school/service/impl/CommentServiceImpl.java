package com.mjc.school.service.impl;

import com.mjc.school.repository.CommentRepository;
import com.mjc.school.repository.model.Comment;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.dto.comment.CommentDtoRequest;
import com.mjc.school.service.dto.comment.CommentDtoResponse;
import com.mjc.school.service.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentDtoResponse> readAll(Pageable pageable) {
        return commentMapper.modelListToDtoList(commentRepository.readAll(pageable));
    }

    @Override
    public Optional<CommentDtoResponse> readById(Long id) {
        return commentRepository.readById(id)
                .map(commentMapper::modelToDto);
    }

    @Override
    @Transactional
    public CommentDtoResponse create(CommentDtoRequest createRequest) {
        return Optional.of(createRequest)
                .map(commentMapper::dtoToModel)
                .map(commentRepository::create)
                .map(commentMapper::modelToDto)
                .orElseThrow();
    }

    @Override
    @Transactional
    public Optional<CommentDtoResponse> update(Long id, CommentDtoRequest updateRequest) {
        return commentRepository.readById(id)
                .map(model -> commentMapper.dtoToModel(updateRequest))
                .map(comment -> {
                    comment.setId(id);
                    return commentRepository.update(comment);
                })
                .map(commentMapper::modelToDto);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return commentRepository.readById(id)
                .map(comment -> commentRepository.deleteById(id))
                .orElse(false);
    }

    @Override
    @Transactional
    public CommentDtoResponse patch(Long id, CommentDtoRequest patchRequest) {
        Comment comment = commentMapper.dtoToModel(patchRequest);
        comment.setId(id);
        return commentMapper.modelToDto(commentRepository.update(comment));
    }

    @Override
    public List<CommentDtoResponse> readAllByNewsId(Long id) {
        return commentMapper.modelListToDtoList(commentRepository.readAllByNewsId(id));
    }
}
