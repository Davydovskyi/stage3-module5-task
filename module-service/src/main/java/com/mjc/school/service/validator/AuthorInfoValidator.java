package com.mjc.school.service.validator;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.service.validator.annotation.AuthorInfo;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class AuthorInfoValidator implements ConstraintValidator<AuthorInfo, Long> {

    private final AuthorRepository authorRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return authorRepository.readById(id).isPresent();
    }
}