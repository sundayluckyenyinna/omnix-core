package com.accionmfb.omnix.core.advice;

import com.accionmfb.omnix.core.annotation.EncryptionPolicyAdvice;
import com.accionmfb.omnix.core.commons.EncryptionPolicy;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.encryption.EncryptionProperties;
import com.accionmfb.omnix.core.encryption.manager.OmnixEncryptionService;
import com.accionmfb.omnix.core.instrumentation.CustomHttpInputMessage;
import com.accionmfb.omnix.core.logger.OmnixHttpLogger;
import com.accionmfb.omnix.core.payload.EncryptionPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@AutoConfiguration
@RestControllerAdvice(basePackages = { "com.accionmfb.omnix" })
@RequiredArgsConstructor
public class OmnixRequestBodyAdvice implements RequestBodyAdvice {

    private final ObjectMapper objectMapper;
    private final OmnixHttpLogger logger;
    private final HttpServletRequest servletRequest;
    private final EncryptionProperties encryptionProperties;
    private final OmnixEncryptionService encryptionService;
    private final static String REQUEST_BODY_KEY = "REQUEST_BODY_KEY";

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Type targetType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(@NonNull HttpInputMessage inputMessage, @NonNull MethodParameter parameter, @NonNull Type targetType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        String requestBody;
        try {
            byte[] bytes = StreamUtils.copyToByteArray(inputMessage.getBody());
            requestBody = new String(bytes);
        }catch (Exception exception){
            log.error("Exception occurred while trying to get request body from InputStream");
            requestBody = StringValues.EMPTY_STRING;
        }
        servletRequest.setAttribute(REQUEST_BODY_KEY, requestBody);
        return new CustomHttpInputMessage(new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8)), inputMessage.getHeaders());
    }

    @Override
    public Object afterBodyRead(@NonNull Object body, @NonNull HttpInputMessage inputMessage, @NonNull MethodParameter parameter, @NonNull Type targetType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        String requestBody = (String) servletRequest.getAttribute(REQUEST_BODY_KEY);
        Method controllerMethod = parameter.getMethod();
        if(Objects.nonNull(controllerMethod)) {
            EncryptionPolicyAdvice encryptionPolicyAdvice = controllerMethod.getAnnotation(EncryptionPolicyAdvice.class);
            if(Objects.nonNull(encryptionPolicyAdvice) && !shouldDecryptRequest(encryptionPolicyAdvice.value())){
                logger.logHttpApiRequest(body, servletRequest, controllerMethod);
                return body;
            }
        }
        if(encryptionProperties.isEnableEncryption()){

           try{
               Class<?> tClazz = body.getClass();
               logger.logHttpApiRequest(requestBody, servletRequest, controllerMethod);
               EncryptionPayload encryptionPayload = objectMapper.readValue(requestBody, EncryptionPayload.class);
               if(Objects.isNull(encryptionPayload) || Objects.isNull(encryptionPayload.getRequest())){
                   log.info("Encrypted Request field has a NULL value: {}", encryptionPayload);
                   return body;
               }else{
                   String decryptedRequest = encryptionService.decrypt(encryptionPayload.getRequest());
                   log.info("Decrypted Request Body: {}", decryptedRequest);
                   return objectMapper.readValue(decryptedRequest, tClazz);
               }
           }catch(Exception exception){
               log.error("Exception occurred while trying to decrypt request body. Exception message is: {}", exception.getMessage());
               return body;
           }
       }
        logger.logHttpApiRequest(requestBody, servletRequest, controllerMethod);
       return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        logger.logHttpApiRequest(body, servletRequest, parameter.getMethod());
        return body;
    }


    private boolean shouldDecryptRequest(EncryptionPolicy encryptionPolicy){
        return encryptionPolicy == EncryptionPolicy.REQUEST || encryptionPolicy == EncryptionPolicy.REQUEST_AND_RESPONSE;
    }
}
