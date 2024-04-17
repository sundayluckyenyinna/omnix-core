package com.accionmfb.omnix.core.validation;


import com.accionmfb.omnix.core.validation.constraints.ListValidator;
import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ListValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsList {
    String message() default "field must be a list";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
