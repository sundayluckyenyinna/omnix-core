package com.accionmfb.omnix.core.encryption;

import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.encryption.EncryptionAlgorithm;
import com.accionmfb.omnix.core.encryption.EncryptionAlgorithmService;
import com.accionmfb.omnix.core.encryption.EncryptionConfig;
import com.accionmfb.omnix.core.encryption.EncryptionProperties;
import com.accionmfb.omnix.core.util.CommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import static com.accionmfb.omnix.core.util.OmnixCoreApplicationUtil.returnOrdefault;

@Slf4j
@Configuration

@RequiredArgsConstructor
@EnableConfigurationProperties(EncryptionProperties.class)
public class AesCBCEncryptionAlgorithmService implements EncryptionAlgorithmService {

    private final ObjectMapper objectMapper;
    private final EncryptionProperties properties;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final static String IV_PARAMETER_KEY = "X-IV";

    @Override
    public String encrypt(String stringToEncrypt) {
        return encryptWithKey(stringToEncrypt, properties.getAesEncryptionKey());
    }

    @Override
    public String encryptWithKey(String stringToEncrypt, String encKey) {
        try {
            byte[] key = encKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8));
            String base64Encrypted = Base64.getEncoder().encodeToString(encrypted);
            String base64IV = Base64.getEncoder().encodeToString(iv);

            httpServletResponse.setHeader(IV_PARAMETER_KEY, base64IV);
            return base64Encrypted;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public String encrypt(String stringToEncrypt, String recipientPublicKeyFile) {
        return encrypt(stringToEncrypt);
    }

    @Override
    public String decrypt(String stringToDecrypt) {
        return decryptWithKey(stringToDecrypt, properties.getAesEncryptionKey());
    }

    @Override
    public String decryptWithKey(String stringToDecrypt, String encKey) {
        try {
            byte[] key = encKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            String cipherInstance = (String) httpServletRequest.getAttribute(StringValues.ENC_CIPHER_KEY);
            Cipher cipher = Cipher.getInstance(CommonUtil.returnOrDefault(cipherInstance, "AES/CBC/PKCS5Padding"));

            String base64IV = httpServletRequest.getHeader(IV_PARAMETER_KEY);
            if (base64IV == null) {
                log.error("IV not provided in request header");
                return null;
            }
            byte[] iv = Base64.getDecoder().decode(base64IV);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decoded = Base64.getDecoder().decode(stringToDecrypt);
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);

        } catch (Exception ex) {
            log.error("Exception occurred while trying to decrypt value: {}", ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public String decryptWithKey(String stringToDecrypt, String encKey, String cipherKey) {
        try {
            byte[] key = encKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance(CommonUtil.returnOrDefault(cipherKey, "AES/CBC/PKCS5Padding"));

            String base64IV = httpServletRequest.getHeader(IV_PARAMETER_KEY);
            if (base64IV == null) {
                log.error("IV not provided in request header");
                return null;
            }
            byte[] iv = Base64.getDecoder().decode(base64IV);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decoded = Base64.getDecoder().decode(stringToDecrypt);
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);

        } catch (Exception ex) {
            log.error("Exception occurred while trying to decrypt value: {}", ex.getMessage(), ex);
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
                .encryptionAlgorithm(EncryptionAlgorithm.AES_CBC.name())
                .encryptionKey(properties.getAesEncryptionKey())
                .publicKeyFile("N/A")
                .privateKeyFile("N/A")
                .build();
    }

    @Override
    public boolean supports(String algorithm) {
        return algorithm.equalsIgnoreCase(EncryptionAlgorithm.AES_CBC.name());
    }
}
