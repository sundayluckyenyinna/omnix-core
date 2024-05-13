package com.accionmfb.omnix.core.advice;

import com.accionmfb.omnix.core.annotation.EncryptionPolicyAdvice;
import com.accionmfb.omnix.core.annotation.ServiceOperation;
import com.accionmfb.omnix.core.commons.EncryptionPolicy;
import com.accionmfb.omnix.core.commons.ResponseCode;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.encryption.EncryptionProperties;
import com.accionmfb.omnix.core.encryption.manager.OmnixEncryptionService;
import com.accionmfb.omnix.core.exception.OmnixApiException;
import com.accionmfb.omnix.core.instrumentation.CustomHttpInputMessage;
import com.accionmfb.omnix.core.logger.OmnixHttpLogger;
import com.accionmfb.omnix.core.payload.EncryptionPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = { "com.accionmfb.omnix" })
public class OmnixRequestBodyAdvice implements RequestBodyAdvice {

    private final ObjectMapper objectMapper;
    private final OmnixHttpLogger logger;
    private final HttpServletRequest servletRequest;
    private final EncryptionProperties encryptionProperties;
    private final OmnixEncryptionService encryptionService;

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
        servletRequest.setAttribute(StringValues.REQUEST_BODY_KEY, requestBody);
        return new CustomHttpInputMessage(new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8)), inputMessage.getHeaders());
    }

    @Override
    public Object afterBodyRead(@NonNull Object body, @NonNull HttpInputMessage inputMessage, @NonNull MethodParameter parameter, @NonNull Type targetType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        String requestBody = (String) servletRequest.getAttribute(StringValues.REQUEST_BODY_KEY);
        Method controllerMethod = parameter.getMethod();
        String encryptionKey = (String) servletRequest.getAttribute(StringValues.ENC_KEY_PLACEHOLDER);
        Boolean decryptionRequired = (Boolean) servletRequest.getAttribute(StringValues.APP_USER_REQUIRE_ENCY_KEY);
        if(Objects.nonNull(controllerMethod)) {
            EncryptionPolicyAdvice encryptionPolicyAdvice = controllerMethod.getAnnotation(EncryptionPolicyAdvice.class);
            if(Objects.nonNull(encryptionPolicyAdvice) && !shouldDecryptRequest(encryptionPolicyAdvice.value())){
                logger.logHttpApiRequest(body, servletRequest, controllerMethod);
                return body;
            }
        }

        boolean encryptionParametersPresent = encryptionProperties.isEnableEncryption() && Objects.nonNull(encryptionKey) && decryptionRequired;
        boolean isControllerPresent = Objects.nonNull(controllerMethod);
        EncryptionRequestMatrix encryptionRequestMatrix = getEncryptionRequestMatrix(controllerMethod);

        logger.logHttpApiRequest(requestBody, servletRequest, controllerMethod);
        if(encryptionParametersPresent && isControllerPresent && encryptionRequestMatrix.isHasRequestBody()){
           try{
               Class<?> tClazz = body.getClass();
               EncryptionPayload encryptionPayload = objectMapper.readValue(requestBody, EncryptionPayload.class);
               if(Objects.isNull(encryptionPayload) || Objects.isNull(encryptionPayload.getRequest())){
                   writeEncryptionViolationResponseToClient();
               }else{
                   String decryptedRequest = encryptionService.decryptWithKey(encryptionPayload.getRequest(), encryptionKey);
                   Object requestBodyObject = objectMapper.readValue(decryptedRequest, tClazz);
                   log.info("Decrypted request: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBodyObject));
                   return requestBodyObject;
               }
           }catch(Exception exception){
               log.error("Exception occurred while trying to decrypt request body. Exception message is: {}", exception.getMessage());
               writeRequestBodyDecryptionDecipherErrorResponseToClient(exception);
           }
       }
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

    @SneakyThrows
    @ServiceOperation(description = "Builds and writes encryption error response to the client on encryption violation")
    private void writeEncryptionViolationResponseToClient(){
        throw OmnixApiException.newInstance()
                .withCode(ResponseCode.FAILED_MODEL)
                .withMessage("Your application channel requires encryption for this resource");
    }

    @SneakyThrows
    @ServiceOperation(description = "Build and throws exception when request advice is not able to decipher or decrypt the request body")
    private void writeRequestBodyDecryptionDecipherErrorResponseToClient(Exception exception){
        throw OmnixApiException.newInstance()
                .withCode(ResponseCode.FAILED_MODEL)
                .withMessage(exception.getMessage());
    }

    @ServiceOperation(description = "Builds and returns the encryption request matrix")
    private EncryptionRequestMatrix getEncryptionRequestMatrix(Method controllerMethod){
        EncryptionRequestMatrix matrix = new EncryptionRequestMatrix();
        if(Objects.isNull(controllerMethod)){
            matrix.setHasRequestBody(false);
            matrix.setRequestBodyClass(null);
            return matrix;
        }
        Parameter[] parameters = controllerMethod.getParameters();
        for(Parameter parameter : parameters){
            RequestBody requestBody = parameter.getDeclaredAnnotation(RequestBody.class);
            if(Objects.nonNull(requestBody)){
                matrix.setHasRequestBody(true);
                matrix.setRequestBodyClass(parameter.getType());
                break;
            }
        }
        return matrix;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EncryptionRequestMatrix {
        private boolean hasRequestBody;
        private Class<?> requestBodyClass;
    }
}
