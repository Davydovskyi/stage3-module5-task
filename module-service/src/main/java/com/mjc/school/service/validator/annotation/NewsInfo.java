package com.mjc.school.service.validator.annotation;

import com.mjc.school.service.validator.NewsInfoValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = NewsInfoValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface NewsInfo {
    String message() default "News Id does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}