package com.accionmfb.omnix.core.encryption.manager;


import com.accionmfb.omnix.core.encryption.EncryptionAlgorithmService;
import lombok.SneakyThrows;

public interface OmnixEncryptionService {
    String encrypt(String algorithm, String stringToEncrypt);

    String encryptWithKey(String algorithm, String stringToEncrypt, String encKey);

    String encrypt(String algorithm, Object object);

    @SneakyThrows
    String encryptWithKey(String algorithm, Object object, String encKey);

    String decrypt(String algorithm, String stringToDecrypt);

    String decryptWithKey(String algorithm, String stringToDecrypt, String encKey);

    String decrypt(String algorithm, Object object);

    @SneakyThrows
    String decryptWithKey(String algorithm, Object object, String encKey);

    EncryptionAlgorithmService getActiveEncryptionAlgorithmService(String algorithm);
}
