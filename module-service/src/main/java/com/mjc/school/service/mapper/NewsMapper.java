package com.mjc.school.service.mapper;

import com.mjc.school.repository.model.News;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.dto.news.NewsDtoRequest;
import com.mjc.school.service.dto.news.NewsDtoResponse;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class, TagMapper.class})
public interface NewsMapper extends BaseMapper<NewsDtoRequest, News, NewsDtoResponse> {

    @Override
    @Mapping(target = "author", source = "author")
    @Mapping(target = "tags", ignore = true)
    @Named("modelToDto")
    NewsDtoResponse modelToDto(News model);

    @Override
    @IterableMapping(qualifiedByName = "modelToDto")
    List<NewsDtoResponse> modelListToDtoList(List<News> models);

    @Mapping(target = "author", source = "author")
    @Mapping(target = "tags", source = "tags")
    @Named("fullModelToDto")
    NewsDtoResponse fullModelToDto(News model);

    @IterableMapping(qualifiedByName = "fullModelToDto")
    List<NewsDtoResponse> fullModelListToDtoList(List<News> models);

    @Override
    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "tags", source = "tagIds")
    News dtoToModel(NewsDtoRequest dtoRequest);

    default List<Tag> mapTagIdsToTagModels(List<Long> tagIds) {
        if (tagIds == null) {
            return List.of();
        }

        return tagIds.stream()
                .map(tagId -> Tag.builder().id(tagId).build())
                .toList();
    }
}