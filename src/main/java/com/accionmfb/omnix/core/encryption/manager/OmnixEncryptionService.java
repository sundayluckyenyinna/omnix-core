package com.accionmfb.omnix.core.encryption.manager;


import com.accionmfb.omnix.core.encryption.EncryptionAlgorithmService;
import lombok.SneakyThrows;

public interface OmnixEncryptionService {
    String encrypt(String stringToEncrypt);

    String encryptWithKey(String stringToEncrypt, String encKey);

    String encrypt(Object object);

    @SneakyThrows
    String encryptWithKey(Object object, String encKey);

    String decrypt(String stringToDecrypt);

    String decryptWithKey(String stringToDecrypt, String encKey);

    String decrypt(Object object);

    @SneakyThrows
    String decryptWithKey(Object object, String encKey);

    String getActiveAlgorithm();

    EncryptionAlgorithmService getActiveEncryptionAlgorithmService(String algorithm);
}
