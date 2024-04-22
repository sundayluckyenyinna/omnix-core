package com.accionmfb.omnix.core.validation.constraints;

import com.accionmfb.omnix.core.validation.OmnixMobileString;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class OmnixMobileNumberValidator implements ConstraintValidator<OmnixMobileString, String> {
    @Override
    public boolean isValid(String mobileNumber, ConstraintValidatorContext constraintValidatorContext) {
        if(Objects.isNull(mobileNumber)){
            return false;
        }
        mobileNumber = mobileNumber.trim();
        if(mobileNumber.length() != 11){
            return false;
        }
        return !mobileNumber.startsWith("+234") && !mobileNumber.startsWith("234");
    }
}
