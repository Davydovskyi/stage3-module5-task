package com.mjc.school.service.validator.annotation;

import com.mjc.school.service.validator.TagsInfoValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = TagsInfoValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface TagsInfo {
    String message() default "Tag Ids does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}