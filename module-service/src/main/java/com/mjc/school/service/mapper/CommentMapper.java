package com.mjc.school.service.mapper;

import com.mjc.school.repository.model.Comment;
import com.mjc.school.service.dto.comment.CommentDtoRequest;
import com.mjc.school.service.dto.comment.CommentDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {NewsMapper.class, AuthorMapper.class, TagMapper.class})
public interface CommentMapper extends BaseMapper<CommentDtoRequest, Comment, CommentDtoResponse> {

    @Override
    @Mapping(target = "news", source = "news")
    CommentDtoResponse modelToDto(Comment model);

    @Override
    @Mapping(target = "news.id", source = "newsId")
    Comment dtoToModel(CommentDtoRequest dtoRequest);
}