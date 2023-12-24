package com.accionmfb.omnix.core.annotation;

import com.accionmfb.omnix.core.commons.LogPolicy;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpLoggingAdvice {
    LogPolicy direction() default LogPolicy.REQUEST_AND_RESPONSE;
}
