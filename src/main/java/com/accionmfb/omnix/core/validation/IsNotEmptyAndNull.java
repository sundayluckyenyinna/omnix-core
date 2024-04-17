package com.accionmfb.omnix.core.validation;

import com.accionmfb.omnix.core.validation.constraints.NullEmptyValidator;
import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NullEmptyValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsNotEmptyAndNull {
    String message() default "field value cannot be null or empty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
