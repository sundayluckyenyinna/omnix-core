package com.accionmfb.omnix.core.advice;

import com.accionmfb.omnix.core.logger.OmnixHttpLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class NoRequestBodyInterceptorRegistry implements WebMvcConfigurer {

    private final OmnixHttpLogger logger;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new NoRequestBodyInterceptorAdvice(logger));
    }
}
