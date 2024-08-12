package com.accionmfb.omnix.core.advice;

import com.accionmfb.omnix.core.annotation.EncryptionPolicyAdvice;
import com.accionmfb.omnix.core.annotation.HttpLoggingAdvice;
import com.accionmfb.omnix.core.commons.EncryptionPolicy;
import com.accionmfb.omnix.core.commons.LogPolicy;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.encryption.EncryptionProperties;
import com.accionmfb.omnix.core.encryption.manager.OmnixEncryptionService;
import com.accionmfb.omnix.core.logger.OmnixHttpLogger;
import com.accionmfb.omnix.core.payload.EncryptionPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = { "com.accionmfb.omnix" })
public class OmnixResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final OmnixHttpLogger logger;
    private final ObjectMapper objectMapper;
    private final OmnixEncryptionService encryptionService;
    private final EncryptionProperties encryptionProperties;


    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    @SneakyThrows
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {
        Object responseObject = body;
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        HttpServletRequest servletRequest = ((ServletServerHttpRequest)request).getServletRequest();
        logger.logHttpApiResponse(body, servletResponse, returnType.getMethod());

        String encryptionKey = (String) servletRequest.getAttribute(StringValues.ENC_KEY_PLACEHOLDER);
        Boolean encryptionRequired = (Boolean) servletRequest.getAttribute(StringValues.APP_USER_REQUIRE_ENCY_KEY);
        Method controllerMethod = returnType.getMethod();
        if(Objects.nonNull(body)) {
            if (encryptionProperties.isEnableEncryption() && Objects.nonNull(encryptionKey) && encryptionRequired) {
                if(Objects.nonNull(controllerMethod)) {
                    EncryptionPolicyAdvice encryptionPolicyAdvice = controllerMethod.getAnnotation(EncryptionPolicyAdvice.class);
                    if(Objects.nonNull(encryptionPolicyAdvice) && !shouldEncryptResponse(encryptionPolicyAdvice.value())){
                        return body;
                    }
                }
                String encryptedResponse = encryptionService.encryptWithKey(body, encryptionKey);
                EncryptionPayload payload = new EncryptionPayload();
                payload.setResponse(encryptedResponse);
                responseObject = payload;
                logEncryptedResponseBody(controllerMethod, payload);
            }
        }
        return responseObject;
    }

    private boolean shouldEncryptResponse(EncryptionPolicy encryptionPolicy){
        return encryptionPolicy == EncryptionPolicy.RESPONSE || encryptionPolicy == EncryptionPolicy.REQUEST_AND_RESPONSE;
    }

    private void logEncryptedResponseBody(Method controllerMethod, EncryptionPayload payload){
        try{
            HttpLoggingAdvice loggingAdvice = controllerMethod.getAnnotation(HttpLoggingAdvice.class);
            if(Objects.nonNull(loggingAdvice)){
                LogPolicy logPolicy = loggingAdvice.direction();
                if(logPolicy == LogPolicy.RESPONSE || logPolicy == LogPolicy.REQUEST_AND_RESPONSE){
                    log.info("Encrypted Response Body: {}", objectMapper.writeValueAsString(payload));
                }
            }else{
                log.info("Encrypted Response Body: {}", objectMapper.writeValueAsString(payload));
            }
        }catch (Exception ignored){}
    }
}
