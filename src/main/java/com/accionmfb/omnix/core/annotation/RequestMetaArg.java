package com.accionmfb.omnix.core.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMetaArg {
    boolean authenticateAppUser() default true;

    boolean authenticateCustomer() default true;
}
