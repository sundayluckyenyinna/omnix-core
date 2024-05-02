package com.accionmfb.omnix.core.advice;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
        NoRequestBodyInterceptorRegistry.class,
        OmnixRequestBodyAdvice.class,
        OmnixResponseBodyAdvice.class,
        OmnixResponseBodyAdvice.class,
        OmnixRestControllerAdvice.class,
        RestControllerIdTokenUnawareConfig.class
})
public class AdviceModule {

}
