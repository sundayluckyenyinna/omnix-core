package com.accionmfb.omnix.core.annotation;

import com.accionmfb.omnix.core.commons.EncryptionPolicy;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptionPolicyAdvice {
    EncryptionPolicy value() default com.accionmfb.omnix.core.commons.EncryptionPolicy.REQUEST_AND_RESPONSE;
}
