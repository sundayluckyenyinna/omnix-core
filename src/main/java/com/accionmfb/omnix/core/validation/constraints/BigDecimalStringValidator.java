package com.accionmfb.omnix.core.validation.constraints;

import com.accionmfb.omnix.core.util.CommonUtil;
import com.accionmfb.omnix.core.validation.IsBigDecimalString;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class BigDecimalStringValidator implements ConstraintValidator<IsBigDecimalString, String> {

    private String max;
    private String min;
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try{
            String value = s.trim();
            BigDecimal bigDecimal = new BigDecimal(value);
            if(!CommonUtil.isNullOrEmpty(min) && CommonUtil.isNullOrEmpty(max)){
                return bigDecimal.compareTo(new BigDecimal(min)) >= 0;
            }
            else if (CommonUtil.isNullOrEmpty(min) && !CommonUtil.isNullOrEmpty(max)){
                return bigDecimal.compareTo(new BigDecimal(max)) <= 0;
            }
            else if(!CommonUtil.isNullOrEmpty(min) && !CommonUtil.isNullOrEmpty(max)){
                return bigDecimal.compareTo(new BigDecimal(min)) >= 0 &&
                        bigDecimal.compareTo(new BigDecimal(max)) <= 0;
            }
            else{
                return true;
            }
        }catch (Exception exception){
            return false;
        }
    }

    @Override
    public void initialize(IsBigDecimalString constraintAnnotation) {
        this.max = constraintAnnotation.max();
        this.min = constraintAnnotation.min();
    }
}
