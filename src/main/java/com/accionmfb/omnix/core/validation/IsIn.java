package com.accionmfb.omnix.core.validation;


import com.accionmfb.omnix.core.validation.constraints.NullEmptyValidator;
import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NullEmptyValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsIn {
    String[] collection();
    String message() default "field must have a value present in constraint collection";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
