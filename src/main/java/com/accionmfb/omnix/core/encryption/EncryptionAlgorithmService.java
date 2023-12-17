package com.accionmfb.omnix.core.encryption;

public interface EncryptionAlgorithmService {

    String encrypt(String stringToEncrypt);

    String encrypt(String stringToEncrypt, String recipientPublicKeyFile);

    String decrypt(String stringToDecrypt);
    String encrypt(Object payload);
    String decrypt(Object payload);
    EncryptionConfig getEncryptionConfiguration();
    boolean supports(String algorithm);
}
