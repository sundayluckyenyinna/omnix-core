package com.accionmfb.omnix.core.encryption;

public interface EncryptionAlgorithmService {

    String encrypt(String stringToEncrypt);

    String encryptWithKey(String stringToEncrypt, String encKey);

    String encrypt(String stringToEncrypt, String recipientPublicKeyFile);

    String decrypt(String stringToDecrypt);

    String decryptWithKey(String stringToDecrypt, String encKey);

    String encrypt(Object payload);

    String encryptWithKey(Object payload, String encKey);

    String decrypt(Object payload);

    String decryptWithKey(Object payload, String encKey);

    EncryptionConfig getEncryptionConfiguration();
    boolean supports(String algorithm);
}
