package com.accionmfb.omnix.core.feign;

import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.encryption.EncryptionProperties;
import com.accionmfb.omnix.core.encryption.manager.OmnixEncryptionService;
import com.accionmfb.omnix.core.logger.OmnixFeignLogger;
import com.accionmfb.omnix.core.payload.EncryptionPayload;
import com.accionmfb.omnix.core.util.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(value = { EncryptionProperties.class })
public class OmnixFeignInterceptor implements ClientHttpRequestInterceptor {

    private final ObjectMapper objectMapper;
    private final OmnixFeignLogger feignLogger;
    private final OmnixEncryptionService encryptionService;
    private final EncryptionProperties encryptionProperties;


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        List<String> encKeyList = request.getHeaders().get(StringValues.ENC_KEY_PLACEHOLDER);
        String encKey = null;
        if(Objects.nonNull(encKeyList)){
             encKey = encKeyList.get(0);
        }

        CommonUtil.runIf(Objects.isNull(encKey), () -> log.warn("Encryption key for encrypting Feign request amd decrypting Feign response is missing"));
        CommonUtil.runIf(Objects.nonNull(encKey), () -> request.getHeaders().remove(StringValues.ENC_KEY_PLACEHOLDER));
        if(encryptionProperties.isEnableEncryption() && Objects.nonNull(encKey)){
            return interceptForEncryptedExecution(encKey, request, body, execution);
        }
        return interceptForRawExecution(request, body, execution);
    }

    private OmnixHttpClientResponse interceptForEncryptedExecution(String encKey, HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String requestJson = new String(body);
        EncryptionPayload payload = EncryptionPayload.withRequest(encryptionService.encryptWithKey(requestJson, encKey));
        String requestPayload = objectMapper.writeValueAsString(payload);

        feignLogger.logHttpFeignRequest(request, requestJson, requestPayload);

        ClientHttpResponse httpResponse = execution.execute(request, requestPayload.getBytes());
        byte[] bytes = StreamUtils.copyToByteArray(httpResponse.getBody());
        String encryptedPayload = new String(bytes);

        EncryptionPayload responsePayload = objectMapper.readValue(encryptedPayload, EncryptionPayload.class);
        String encResponseJson = responsePayload.getResponse();
        String decryptedResponseJson = encryptionService.decryptWithKey(encResponseJson, encKey);
        OmnixHttpClientResponse response = new OmnixHttpClientResponse(httpResponse, decryptedResponseJson, encryptedPayload);
        feignLogger.logHttpFeignResponse(response);
        return response;
    }

    private ClientHttpResponse interceptForRawExecution(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        return execution.execute(request, body);
    }
}
