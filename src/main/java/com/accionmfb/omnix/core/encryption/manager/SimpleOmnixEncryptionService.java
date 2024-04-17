package com.accionmfb.omnix.core.encryption.manager;


import com.accionmfb.omnix.core.commons.OmnixParam;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.encryption.AesEncryptionAlgorithmService;
import com.accionmfb.omnix.core.encryption.EncryptionAlgorithmService;
import com.accionmfb.omnix.core.encryption.EncryptionProperties;
import com.accionmfb.omnix.core.localsource.core.LocalParamStorage;
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
    private final LocalParamStorage cacheStorage;
    private final ObjectMapper objectMapper;
    private final EncryptionProperties encryptionProperties;

    @Override
    public String encrypt(String stringToEncrypt){
        if(Objects.nonNull(stringToEncrypt)) {
            String activeAlgorithm = getActiveAlgorithm();
            return getActiveEncryptionAlgorithmService(activeAlgorithm).encrypt(stringToEncrypt);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    public String encryptWithKey(String stringToEncrypt, String encKey){
        if(Objects.nonNull(stringToEncrypt)) {
            String activeAlgorithm = getActiveAlgorithm();
            return getActiveEncryptionAlgorithmService(activeAlgorithm).encryptWithKey(stringToEncrypt, encKey);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    @SneakyThrows
    public String encrypt(Object object) {
        if(Objects.nonNull(object)) {
            String stringToEncrypt = object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
            return encrypt(stringToEncrypt);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    @SneakyThrows
    public String encryptWithKey(Object object, String encKey) {
        if(Objects.nonNull(object)) {
            String stringToEncrypt = object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
            return encryptWithKey(stringToEncrypt, encKey);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    public String decrypt(String stringToDecrypt) {
        if(Objects.nonNull(stringToDecrypt)){
            String activeAlgorithm = getActiveAlgorithm();
            return getActiveEncryptionAlgorithmService(activeAlgorithm).decrypt(stringToDecrypt);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    public String decryptWithKey(String stringToDecrypt, String encKey) {
        if(Objects.nonNull(stringToDecrypt)){
            String activeAlgorithm = getActiveAlgorithm();
            return getActiveEncryptionAlgorithmService(activeAlgorithm).decryptWithKey(stringToDecrypt, encKey);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    @SneakyThrows
    public String decrypt(Object object) {
        if(Objects.nonNull(object)){
            String stringToDecrypt = object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
            return decrypt(stringToDecrypt);
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    @SneakyThrows
    public String decryptWithKey(Object object, String encKey) {
        if(Objects.nonNull(object)){
            String stringToDecrypt = object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
            return decryptWithKey(stringToDecrypt, encKey);
        }
        return StringValues.EMPTY_STRING;
    }


    @Override
    public String getActiveAlgorithm(){
        return cacheStorage.getParamValueOrDefault(OmnixParam.OMNIX_ENCRYPTION_ALGORITHM, encryptionProperties.getAlgorithm(), true);
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
        return algorithmService;
    }
}
