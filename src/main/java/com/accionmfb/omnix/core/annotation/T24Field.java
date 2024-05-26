package com.accionmfb.omnix.core.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface T24Field {
    String key();
}
