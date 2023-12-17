package com.accionmfb.omnix.core.advice;

import com.accionmfb.omnix.core.logger.OmnixHttpLogger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
public class NoRequestBodyInterceptorAdvice implements HandlerInterceptor, WebMvcConfigurer {

    private final OmnixHttpLogger logger;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if(request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())){
            logger.logHttpApiRequest(null, request);
        }
        return true;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new NoRequestBodyInterceptorAdvice(logger));
    }
}
