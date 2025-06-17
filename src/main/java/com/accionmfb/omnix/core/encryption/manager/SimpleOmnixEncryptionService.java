package com.accionmfb.omnix.core.encryption.manager;


import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.encryption.AesEncryptionAlgorithmService;
import com.accionmfb.omnix.core.encryption.EncryptionAlgorithmService;
import com.accionmfb.omnix.core.encryption.EncryptionProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;

@Slf4j
@Configuration

@RequiredArgsConstructor
@EnableConfigurationProperties(value = EncryptionProperties.class)
public class SimpleOmnixEncryptionService implements OmnixEncryptionService{

    private final List<EncryptionAlgorithmService> encryptionAlgorithmServices;
    private final AesEncryptionAlgorithmService defaultAlgorithm;
    private final ObjectMapper objectMapper;

    @Override
    public String encrypt(String algorithm, String stringToEncrypt){
        if(Objects.nonNull(stringToEncrypt)) {
            return getActiveEncryptionAlgorithmService(algorithm).encrypt(stringToEncrypt);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    public String encryptWithKey(String algorithm, String stringToEncrypt, String encKey){
        if(Objects.nonNull(stringToEncrypt)) {
            return getActiveEncryptionAlgorithmService(algorithm).encryptWithKey(stringToEncrypt, encKey);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    @SneakyThrows
    public String encrypt(String algorithm, Object object) {
        if(Objects.nonNull(object)) {
            String stringToEncrypt = object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
            return encrypt(algorithm, stringToEncrypt);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    @SneakyThrows
    public String encryptWithKey(String algorithm, Object object, String encKey) {
        if(Objects.nonNull(object)) {
            String stringToEncrypt = object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
            return encryptWithKey(algorithm, stringToEncrypt, encKey);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    public String decrypt(String algorithm, String stringToDecrypt) {
        if(Objects.nonNull(stringToDecrypt)){
            return getActiveEncryptionAlgorithmService(algorithm).decrypt(stringToDecrypt);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    public String decryptWithKey(String algorithm, String stringToDecrypt, String encKey) {
        if(Objects.nonNull(stringToDecrypt)){
            return getActiveEncryptionAlgorithmService(algorithm).decryptWithKey(stringToDecrypt, encKey);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    @SneakyThrows
    public String decrypt(String algorithm, Object object) {
        if(Objects.nonNull(object)){
            String stringToDecrypt = object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
            return decrypt(algorithm, stringToDecrypt);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    @SneakyThrows
    public String decryptWithKey(String algorithm, Object object, String encKey) {
        if(Objects.nonNull(object)){
            String stringToDecrypt = object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
            return decryptWithKey(algorithm, stringToDecrypt, encKey);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    public EncryptionAlgorithmService getActiveEncryptionAlgorithmService(String algorithm){
        EncryptionAlgorithmService algorithmService = encryptionAlgorithmServices
                .stream()
                .filter(encryptionAlgorithmService -> encryptionAlgorithmService.supports(algorithm))
                .findFirst()
                .orElse(null);
        if(Objects.isNull(algorithmService)){
            log.warn("No algorithm service implementing {} algorithm. Omnix will resolve to the default algorithm of {}", algorithm, defaultAlgorithm.getClass().getSimpleName());
            return defaultAlgorithm;
        }
        log.info("Found algorithm implementation: {}", algorithmService.getClass().getSimpleName());
        return algorithmService;
    }
}
