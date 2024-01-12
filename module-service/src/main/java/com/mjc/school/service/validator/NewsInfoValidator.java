package com.mjc.school.service.validator;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.service.validator.annotation.NewsInfo;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class NewsInfoValidator implements ConstraintValidator<NewsInfo, Long> {

    private final NewsRepository newsRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return newsRepository.readById(id).isPresent();
    }
}