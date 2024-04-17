package com.accionmfb.omnix.core.logger;

import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.feign.OmnixHttpClientResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class OmnixFeignLogger implements Loggable{

    private final ObjectMapper objectMapper;

    public void logHttpFeignRequest(HttpRequest request, Object requestBody, String encryptedBody){
        try {
            System.out.println();
            log.info("=============================================  INTERNAL SERVICE REQUEST START ======================================================");
            log.info("Request URI: {} {}", request.getMethod().name(), request.getURI());
            writeBodyByLogStyle(requestBody);
            log.info("Encrypted Request: {}", encryptedBody);
            try { log.info("Request Headers: {}", objectMapper.writeValueAsString(request.getHeaders())); } catch (Exception exception){}
            log.info("=======================================================================================================================");
        }catch (Exception ignored){}
    }

    public void logHttpFeignResponse(OmnixHttpClientResponse clientHttpResponse){
        try {
            System.out.println();
            log.info("=============================================  INTERNAL SERVICE RESPONSE END ======================================================");
            log.info("Response Status: {} {}", clientHttpResponse.getStatusText(), clientHttpResponse.getStatusCode().value());
            writeBodyByLogStyle(clientHttpResponse.getEncryptedBody());
            log.info("Decrypted Body: {}", clientHttpResponse.getBodyString());
            log.info("Response Headers: {}", clientHttpResponse.getHeaders());
            log.info("=======================================================================================================================");
            System.out.println();
        }catch (Exception ignored){}
    }

    private void writeBodyByLogStyle(Object body) throws JsonProcessingException {
        log.info("Body: {}", Objects.isNull(body) ? StringValues.EMPTY_STRING : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
    }
}
