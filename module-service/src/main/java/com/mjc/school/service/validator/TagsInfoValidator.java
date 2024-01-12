package com.mjc.school.service.validator;

import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.validator.annotation.TagsInfo;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TagsInfoValidator implements ConstraintValidator<TagsInfo, List<Long>> {

    private final TagRepository tagRepository;

    @Override
    public boolean isValid(List<Long> ids, ConstraintValidatorContext constraintValidatorContext) {
        Set<Long> existingIds = tagRepository.readAllByIds(ids).stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());

        return existingIds.containsAll(ids);
    }
}