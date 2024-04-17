package com.accionmfb.omnix.core.validation.constraints;

import com.accionmfb.omnix.core.validation.IsNotEmptyAndNull;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Objects;

public class NullEmptyValidator implements ConstraintValidator<IsNotEmptyAndNull, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return Objects.nonNull(s) && !s.trim().isEmpty();
    }
}
