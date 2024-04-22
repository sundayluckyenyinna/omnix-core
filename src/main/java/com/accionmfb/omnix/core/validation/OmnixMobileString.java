package com.accionmfb.omnix.core.validation;


import com.accionmfb.omnix.core.validation.constraints.NumberStringValidator;
import com.accionmfb.omnix.core.validation.constraints.OmnixMobileNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OmnixMobileNumberValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OmnixMobileString {

    String message() default "mobileNumber must be a valid nigerian mobile number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
