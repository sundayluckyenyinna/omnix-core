package com.accionmfb.omnix.core.logger;

import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.feign.OmnixHttpClientResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestTemplate;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class OmnixFeignLogger implements Loggable{

    private final ObjectMapper objectMapper;

    public void logHttpFeignRequest(RequestTemplate request, Object requestBody, String encryptedBody){
        try {
            System.out.println();
            log.info("=============================================  INTERNAL SERVICE REQUEST START ======================================================");
            log.info("Request URI: {} {}", request.method(), request.url());
            writeBodyByLogStyle(requestBody);
            log.info("Encrypted Request: {}", encryptedBody);
            try { log.info("Request Headers: {}", objectMapper.writeValueAsString(request.headers())); } catch (Exception ignored){}
            log.info("=======================================================================================================================");
        }catch (Exception ignored){}
    }

    public void logHttpFeignResponse(Response clientHttpResponse, String body, String decryptedBody){
        try {
            System.out.println();
            log.info("=============================================  INTERNAL SERVICE RESPONSE END ======================================================");
            log.info("Response Status: {}", HttpStatus.resolve(clientHttpResponse.status()));
            writeBodyByLogStyle(body);
            log.info("Decrypted Body: {}", decryptedBody);
            log.info("Response Headers: {}", clientHttpResponse.headers());
            log.info("=======================================================================================================================");
            System.out.println();
        }catch (Exception ignored){}
    }

    private void writeBodyByLogStyle(Object body) throws JsonProcessingException {
        log.info("Body: {}", Objects.isNull(body) ? StringValues.EMPTY_STRING : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
    }
}
