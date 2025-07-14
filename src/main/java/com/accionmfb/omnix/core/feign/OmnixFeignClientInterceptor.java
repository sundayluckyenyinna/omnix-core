package com.accionmfb.omnix.core.feign;

import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.encryption.EncryptionProperties;
import com.accionmfb.omnix.core.encryption.manager.OmnixEncryptionService;
import com.accionmfb.omnix.core.localsource.core.LocalParamStorage;
import com.accionmfb.omnix.core.logger.OmnixFeignLogger;
import com.accionmfb.omnix.core.payload.EncryptionPayload;
import com.accionmfb.omnix.core.util.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Component
public class OmnixFeignClientInterceptor extends SpringDecoder implements RequestInterceptor {
    private final ObjectMapper objectMapper;
    private final OmnixFeignLogger feignLogger;
    private final LocalParamStorage localParamStorage;
    private final OmnixEncryptionService encryptionService;
    private final EncryptionProperties encryptionProperties;

    public OmnixFeignClientInterceptor(ObjectFactory<HttpMessageConverters> messageConverters, ObjectMapper objectMapper, OmnixFeignLogger feignLogger, OmnixEncryptionService encryptionService, EncryptionProperties encryptionProperties, LocalParamStorage localParamStorage) {
        super(messageConverters);
        this.objectMapper = objectMapper;
        this.feignLogger = feignLogger;
        this.encryptionService = encryptionService;
        this.encryptionProperties = encryptionProperties;
        this.localParamStorage = localParamStorage;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (requestTemplate.body() != null) {
            byte[] rawRequestBodyBytes = requestTemplate.body();
            String rawRequestBodyJson = new String(rawRequestBodyBytes);
            HttpServletRequest servletRequest=null;
            String encryptionAlgorithm="";
            try {
                String encryptionKey = requestTemplate.headers()
                        .get(StringValues.ENC_KEY_PLACEHOLDER)
                        .stream()
                        .findFirst()
                        .orElse(null);
                try {
                    servletRequest = ((ServletRequestAttributes) RequestContextHolder
                            .getRequestAttributes()).getRequest();
                    encryptionAlgorithm = (String) servletRequest.getAttribute(StringValues.APP_USER_ENCRYPTION_ALGORITHM);
                } catch (Exception e) {
                    log.info("Fall back");
                    encryptionAlgorithm =requestTemplate.headers()
                            .get(StringValues.APP_USER_ENCRYPTION_ALGORITHM).stream().findFirst().orElse(null);
                    log.info("Picked: {}", encryptionAlgorithm);
                }

                Collection<String> headerValues = requestTemplate.headers()
                        .get(StringValues.APP_USER_REQUIRE_ENCY_KEY);

                boolean encryptionRequired = Objects.isNull(headerValues)
                        || headerValues.isEmpty()
                        || headerValues.stream().anyMatch(value -> value.equalsIgnoreCase("true"));

                if (encryptionProperties.isEnableEncryption()
                        && !CommonUtil.isNullOrEmpty(encryptionKey)
                        && encryptionRequired) {

                    String encryptedRequest = encryptionService.encryptWithKey(encryptionAlgorithm, rawRequestBodyJson, encryptionKey);
                    EncryptionPayload payload = EncryptionPayload.withRequest(encryptedRequest);
                    String payloadJson = objectMapper.writeValueAsString(payload);

                   try {
                       log.info(">>>>>>>>>>>>>3>>>>>>>>>>>>>>" + servletRequest.getHeader("X-IV"));
                       log.info(">>>>>>>>>>>>>4>>>>>>>>>>>>>>" + (String) servletRequest.getAttribute("X-IV"));
                       String iv = CommonUtil.returnOrDefault((String) servletRequest.getAttribute("X-IV"), servletRequest.getHeader("X-IV"));

                       log.info("Print iv value: {}", iv);

                       if (iv != null) {
                           log.info("Catch you Iv is present: ------- {}", iv);
                           requestTemplate.header("X-IV", iv);
                       }
                   } catch (Exception e) {
                       log.info("Iv is not present");
                   }

                    requestTemplate.body(payloadJson);
                    // Optionally log encrypted payload
                    // feignLogger.logHttpFeignRequest(requestTemplate, rawRequestBodyJson, payloadJson);
                } else {
                    // feignLogger.logHttpFeignRequest(requestTemplate, rawRequestBodyJson, StringValues.EMPTY_STRING);
                }
            } catch (Exception exception) {
                log.error("Exception occurred while intercepting feign request body");
                log.error("Exception message is: {}", exception.getMessage());
            }
        }
    }


    @Override
    public Object decode(final Response response, Type type) throws IOException, FeignException {
        byte[] bodyStream = response.body().asInputStream().readAllBytes();
        String responseBody = new String(bodyStream);
        HttpServletRequest servletRequest;
        String encryptionAlgorithm="";
        String iv=null;
        String encryptionKey = response.request().headers().get(StringValues.ENC_KEY_PLACEHOLDER)
                .stream().findFirst().orElse(null);
        try {
            iv = response.headers().get("X-IV").stream().findFirst().orElse(null);
            log.info("Print iv value here used: {}", iv);
            servletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            encryptionAlgorithm = (String) servletRequest.getAttribute(StringValues.APP_USER_ENCRYPTION_ALGORITHM);
        } catch (Exception e) {
            encryptionAlgorithm=response.request().headers().get(StringValues.APP_USER_ENCRYPTION_ALGORITHM)
                    .stream().findFirst().orElse(null);
        }

        Collection<String> headerValues  = response.request().headers().get(StringValues.APP_USER_REQUIRE_ENCY_KEY);
        boolean encryptionRequired = Objects.isNull(headerValues) || headerValues.isEmpty() || headerValues.stream().anyMatch(value -> value.equalsIgnoreCase("true"));
        if(encryptionProperties.isEnableEncryption() && !CommonUtil.isNullOrEmpty(encryptionKey) && encryptionRequired){
            EncryptionPayload encryptionPayload = objectMapper.readValue(responseBody, EncryptionPayload.class);
            String encryptedResponse = encryptionPayload.getResponse();
            String decryptedResponseBody = iv==null? encryptionService.decryptWithKey(encryptionAlgorithm, encryptedResponse, encryptionKey):encryptionService.decryptWithKey(encryptionAlgorithm, encryptedResponse, encryptionKey, iv);
//            feignLogger.logHttpFeignResponse(response, responseBody, decryptedResponseBody);
            return objectMapper.readValue(decryptedResponseBody, objectMapper.constructType(type));
        }else {
//            feignLogger.logHttpFeignResponse(response, responseBody, StringValues.EMPTY_STRING);
            return objectMapper.readValue(responseBody, objectMapper.constructType(type));
        }
    }
}
