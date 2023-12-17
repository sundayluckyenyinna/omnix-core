package com.accionmfb.omnix.core.advice;

import com.accionmfb.omnix.core.annotation.EncryptionPolicyAdvice;
import com.accionmfb.omnix.core.commons.EncryptionPolicy;
import com.accionmfb.omnix.core.encryption.EncryptionProperties;
import com.accionmfb.omnix.core.encryption.manager.OmnixEncryptionService;
import com.accionmfb.omnix.core.logger.OmnixHttpLogger;
import com.accionmfb.omnix.core.payload.EncryptionPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = { "com.accionmfb.omnix" })
public class OmnixResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final EncryptionProperties encryptionProperties;
    private final OmnixEncryptionService encryptionService;
    private final ObjectMapper objectMapper;
    private final OmnixHttpLogger logger;

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
        logger.logHttpApiResponse(body, servletResponse);

        Method controllerMethod = returnType.getMethod();
        if(Objects.nonNull(body)) {
            if (encryptionProperties.isEnableEncryption()) {
                if(Objects.nonNull(controllerMethod)) {
                    EncryptionPolicyAdvice encryptionPolicyAdvice = controllerMethod.getAnnotation(EncryptionPolicyAdvice.class);
                    if(Objects.nonNull(encryptionPolicyAdvice) && !shouldEncryptResponse(encryptionPolicyAdvice.value())){
                        return body;
                    }
                }
                String encryptedResponse = encryptionService.encrypt(body);
                EncryptionPayload payload = new EncryptionPayload();
                payload.setResponse(encryptedResponse);
                responseObject = payload;
                String json = objectMapper.writeValueAsString(responseObject);
                responseObject = body instanceof String ? json : objectMapper.readValue(json, body.getClass());
                log.info("Encrypted Response Body: {}", objectMapper.writeValueAsString(responseObject));
            }
        }
        return responseObject;
    }

    private boolean shouldEncryptResponse(EncryptionPolicy encryptionPolicy){
        return encryptionPolicy == EncryptionPolicy.RESPONSE || encryptionPolicy == EncryptionPolicy.REQUEST_AND_RESPONSE;
    }
}
