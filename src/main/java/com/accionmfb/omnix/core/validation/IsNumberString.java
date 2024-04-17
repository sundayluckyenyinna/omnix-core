package com.accionmfb.omnix.core.validation;


import com.accionmfb.omnix.core.validation.constraints.NullEmptyValidator;
import com.accionmfb.omnix.core.validation.constraints.NumberStringValidator;
import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NumberStringValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsNumberString {
    String message() default "field must be a number string";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
