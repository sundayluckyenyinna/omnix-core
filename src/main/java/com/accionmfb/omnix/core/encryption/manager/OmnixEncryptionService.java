package com.accionmfb.omnix.core.encryption.manager;


import com.accionmfb.omnix.core.encryption.EncryptionAlgorithmService;

public interface OmnixEncryptionService {
    String encrypt(String stringToEncrypt);
    String encrypt(Object object);
    String decrypt(String stringToDecrypt);
    String decrypt(Object object);

    String getActiveAlgorithm();

    EncryptionAlgorithmService getActiveEncryptionAlgorithmService(String algorithm);
}
