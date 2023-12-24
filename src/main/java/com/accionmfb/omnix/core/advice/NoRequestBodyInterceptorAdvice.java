package com.accionmfb.omnix.core.advice;

import com.accionmfb.omnix.core.logger.OmnixHttpLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
public class NoRequestBodyInterceptorAdvice implements HandlerInterceptor{

    private final OmnixHttpLogger logger;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if(handler instanceof HandlerMethod) {
            if (request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
                logger.logHttpApiRequest(null, request, ((HandlerMethod) handler).getMethod());
            }
        }
        return true;
    }
}
