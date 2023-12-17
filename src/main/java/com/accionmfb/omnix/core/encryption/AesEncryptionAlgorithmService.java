package com.accionmfb.omnix.core.encryption;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
@Configuration
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(EncryptionProperties.class)
public class AesEncryptionAlgorithmService implements EncryptionAlgorithmService {

    private final ObjectMapper objectMapper;
    private final EncryptionProperties properties;

    @Override
    public String encrypt(String stringToEncrypt) {
        try {
            byte[] key = properties.getAesEncryptionKey().getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 InvalidKeyException ex) {
            log.error("Exception occurred while trying to encrypt value: {}", ex.getMessage());
        }
        return null;
    }

    @Override
    public String encrypt(String stringToEncrypt, String recipientPublicKeyFile) {
        return encrypt(stringToEncrypt);
    }

    @Override
    public String decrypt(String stringToDecrypt) {
        try {
            byte[] key = properties.getAesEncryptionKey().getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)));
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 BadPaddingException ex) {
            log.error("Exception occurred while trying to decrypt value: {}", ex.getMessage());
        }
        return null;
    }

    @Override
    public String encrypt(Object payload) {
        try {
            String textToEncrypt = objectMapper.writeValueAsString(payload);
            return encrypt(textToEncrypt);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }

    @Override
    public String decrypt(Object payload) {
        try {
            return decrypt(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }

    @Override
    public EncryptionConfig getEncryptionConfiguration() {
        return EncryptionConfig.builder()
                .encryptionAlgorithm(EncryptionAlgorithm.AES.name())
                .encryptionKey(properties.getAesEncryptionKey())
                .publicKeyFile("N/A")
                .privateKeyFile("N/A")
                .build();
    }

    @Override
    public boolean supports(String algorithm) {
        return algorithm.equalsIgnoreCase(EncryptionAlgorithm.AES.toString());
    }
}
