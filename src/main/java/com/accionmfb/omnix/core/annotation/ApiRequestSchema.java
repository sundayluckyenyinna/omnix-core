package com.accionmfb.omnix.core.annotation;


import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@RequestBody
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.CLASS)
public @interface ApiRequestSchema {

    @AliasFor(value = "description", annotation = RequestBody.class)
    String description() default "";

    @AliasFor(value = "content", annotation = RequestBody.class)
    Content[] content() default {};

    @AliasFor(value = "required", annotation = RequestBody.class)
    boolean required() default false;

    @AliasFor(value = "extensions", annotation = RequestBody.class)
    Extension[] extensions() default {};

    @AliasFor(value = "ref", annotation = RequestBody.class)
    String ref() default "";
}
