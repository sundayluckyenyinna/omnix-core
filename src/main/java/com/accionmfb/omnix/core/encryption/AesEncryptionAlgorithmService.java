package com.accionmfb.omnix.core.encryption;

import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.util.CommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import static com.accionmfb.omnix.core.util.OmnixCoreApplicationUtil.returnOrdefault;

@Slf4j
@Configuration

@RequiredArgsConstructor
@EnableConfigurationProperties(EncryptionProperties.class)
public class AesEncryptionAlgorithmService implements EncryptionAlgorithmService {

    private final ObjectMapper objectMapper;
    private final EncryptionProperties properties;
    private final HttpServletRequest httpServletRequest;

    @Override
    public String encrypt(String stringToEncrypt) {
        try {
            byte[] key = properties.getAesEncryptionKey().getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            String cipherInstance = (String) httpServletRequest.getAttribute(StringValues.ENC_CIPHER_KEY);
            Cipher cipher = Cipher.getInstance(CommonUtil.returnOrDefault(cipherInstance,"AES/GCM/NoPadding"));
            IvParameterSpec ivParameterSpec = generateNewIvSpec();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,ivParameterSpec);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8))).concat(":").concat(Base64.getEncoder().encodeToString(ivParameterSpec.getIV()));
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 InvalidAlgorithmParameterException| InvalidKeyException ex) {
            log.error("Exception occurred while trying to encrypt value: {}", ex.getMessage());
        }
        return null;
    }

    @Override
    public String encryptWithKey(String stringToEncrypt, String encKey) {
        try {
            byte[] key = returnOrdefault(encKey, properties.getAesEncryptionKey()).getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            String cipherInstance = (String) httpServletRequest.getAttribute(StringValues.ENC_CIPHER_KEY);
            Cipher cipher = Cipher.getInstance(CommonUtil.returnOrDefault(cipherInstance,"AES/ECB/PKCS5Padding"));
            IvParameterSpec ivParameterSpec = generateNewIvSpec();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8))).concat(":").concat(Base64.getEncoder().encodeToString(ivParameterSpec.getIV()));
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 InvalidAlgorithmParameterException|InvalidKeyException ex) {
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

            String cipherInstance = (String) httpServletRequest.getAttribute(StringValues.ENC_CIPHER_KEY);
            Cipher cipher = Cipher.getInstance(CommonUtil.returnOrDefault(cipherInstance,"AES/ECB/PKCS5PADDING"));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, getIvParameterSpec());
            return new String(cipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)));
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 InvalidAlgorithmParameterException| BadPaddingException ex) {
            log.error("Exception occurred while trying to decrypt value: {}", ex.getMessage());
        }
        return null;
    }

    @Override
    public String decryptWithKey(String stringToDecrypt, String encKey) {
        try {
            byte[] key = returnOrdefault(encKey, properties.getAesEncryptionKey()).getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            String cipherInstance = (String) httpServletRequest.getAttribute(StringValues.ENC_CIPHER_KEY); // It must be provided
            Cipher cipher = Cipher.getInstance(CommonUtil.returnOrDefault(cipherInstance,"AES/ECB/PKCS5PADDING"));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, getIvParameterSpec());
            return new String(cipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)));
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 InvalidAlgorithmParameterException | BadPaddingException ex) {
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
    public String encryptWithKey(Object payload, String encKey) {
        try {
            String textToEncrypt = objectMapper.writeValueAsString(payload);
            return encryptWithKey(textToEncrypt, encKey);
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
    public String decryptWithKey(Object payload, String encKey) {
        try {
            return decryptWithKey(objectMapper.writeValueAsString(payload), encKey);
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

    @Override
    public IvParameterSpec getIvParameterSpec() {
        String cipherIv = (String) httpServletRequest.getAttribute(StringValues.ENC_CIPHER_IV);
        return new IvParameterSpec(cipherIv.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public IvParameterSpec generateNewIvSpec(){
        byte[] iv = new byte[12];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}
