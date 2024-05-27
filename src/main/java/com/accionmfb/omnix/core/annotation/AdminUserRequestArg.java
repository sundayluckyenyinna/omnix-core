package com.accionmfb.omnix.core.annotation;

import springfox.documentation.annotations.ApiIgnore;

import java.lang.annotation.*;

@Documented
@ApiIgnore
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminUserRequestArg {

    boolean authenticate() default true;
}
