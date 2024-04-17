package com.accionmfb.omnix.core.validation.constraints;

import com.accionmfb.omnix.core.validation.IsList;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.List;

public class ListValidator implements ConstraintValidator<IsList, Object> {
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        return object.getClass().isAssignableFrom(List.class);
    }
}
