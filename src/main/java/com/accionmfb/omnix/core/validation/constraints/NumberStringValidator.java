package com.accionmfb.omnix.core.validation.constraints;

import com.accionmfb.omnix.core.validation.IsNumberString;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class NumberStringValidator implements ConstraintValidator<IsNumberString, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return Objects.nonNull(s) && !s.isEmpty() && StringUtils.isNumeric(s);
    }
}
