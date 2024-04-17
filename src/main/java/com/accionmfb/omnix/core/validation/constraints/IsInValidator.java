package com.accionmfb.omnix.core.validation.constraints;

import com.accionmfb.omnix.core.validation.IsIn;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class IsInValidator implements ConstraintValidator<IsIn, String> {

    private Collection<?> collection;

    @Override
    public void initialize(IsIn constraintAnnotation){
        this.collection = List.of(constraintAnnotation.collection());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return Objects.nonNull(value) && collection.contains(value);
    }
}
