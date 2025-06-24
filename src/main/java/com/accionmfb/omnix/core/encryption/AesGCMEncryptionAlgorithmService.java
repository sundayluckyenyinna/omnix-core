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
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(EncryptionProperties.class)
public class AesGCMEncryptionAlgorithmService implements EncryptionAlgorithmService {

    private final ObjectMapper objectMapper;
    private final EncryptionProperties properties;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final static String IV_PARAMETER_KEY = "X-IV";
    private final static String AUTH_TAG_PARAMETER_KEY = "X-TAG";

    @Override
    public String encrypt(String stringToEncrypt) {
        return encryptWithKey(stringToEncrypt, properties.getAesEncryptionKey());
    }

    @Override
    public String encryptWithKey(String stringToEncrypt, String encKey) {
        try {
            // Generate random IV
            byte[] ivBytes = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(ivBytes);

            // Step 1: Convert the plain key (32-byte) and Base64 IV (16 bytes) to byte arrays
            byte[] keyBytes = encKey.getBytes(StandardCharsets.UTF_8);  // Key is a 32-byte plain string

            // Step 2: Create SecretKeySpec from the provided 32-byte key
            SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            // Send the IV to the response headers
            httpServletResponse.setHeader(IV_PARAMETER_KEY, Base64.getEncoder().encodeToString(ivBytes));
            httpServletRequest.setAttribute(IV_PARAMETER_KEY, Base64.getEncoder().encodeToString(ivBytes));

            System.out.println(">>>>>>>>>>1>>>>>>>>>>>> "+ Base64.getEncoder().encodeToString(ivBytes));

            // Encrypt the plaintext
            byte[] encryptedBytes = cipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
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
            // Get the base64 IV
            String iv = httpServletRequest.getHeader(IV_PARAMETER_KEY);

            System.out.println("x-IV in request: ==============>" + iv);
            System.out.println("Encryption key in request: =========>" + encKey);
            System.out.println("String to encrypt: =========>" + stringToDecrypt);

            return decryptWithKeyStatic(stringToDecrypt, encKey, iv);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String decryptWithKey(String stringToDecrypt, String encKey, String iv) {
        try {

            System.out.println("x-IV in request: ==============>" + iv);
            System.out.println("Encryption key in request: =========>" + encKey);
            System.out.println("String to encrypt: =========>" + stringToDecrypt);

            return decryptWithKeyStatic(stringToDecrypt, encKey, iv);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        return algorithm.equalsIgnoreCase(EncryptionAlgorithm.AES_GCM.name());
    }

    public static String encryptWithKeyStatic(String stringToEncrypt, String encKey) {
        try {

            byte[] ivBytes = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(ivBytes);

            // Step 1: Convert the plain key (32-byte) and Base64 IV (16 bytes) to byte arrays
            byte[] keyBytes = encKey.getBytes(StandardCharsets.UTF_8);  // Key is a 32-byte plain string

            // Step 2: Create SecretKeySpec from the provided 32-byte key
            SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            // Encrypt the plaintext
            byte[] encryptedBytes = cipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptWithKeyStatic(String stringToDecrypt, String encKey, String iv) {
        System.out.println(">>>>>>>>>>2>>>>>>>>>>>> "+ iv);
        try {
            // Step 1: Convert the plain key (32-byte) and Base64 IV (16 bytes) to byte arrays
            byte[] keyBytes = encKey.getBytes(StandardCharsets.UTF_8);  // Key is a 32-byte plain string
            byte[] ivBytes = Base64.getDecoder().decode(iv);  // Decode the 16-character Base64 encoded IV

            // Step 2: Create SecretKeySpec from the provided 32-byte key
            SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");

            // Step 3: Initialize Cipher for AES/GCM/NoPadding decryption
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            // Step 4: Decode the Base64-encoded ciphertext
            byte[] encryptedBytes = Base64.getDecoder().decode(stringToDecrypt);

            // Step 5: Decrypt the ciphertext to get the original plaintext
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Step 6: Return the decrypted plaintext as a string
            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws JSONException {
        String iv = "eG90XG9uanh2aHdZa3dbcA=="; // eG90XG9uanh2aHdZa3dbcA==
        String encKey = "77T18925x42783H7508302949Q618671";
//        String text = "X4h4e/mjomrIkVqY3+N6VDR41f9beoBSqIz2wnxHeAdXl2beheBN0jZJk3t307kujfv4cTvUCYObQNxPUyq5OuuW3JO+sxjvBzBHT6dcMSCPi6h9w961hagknVJcUhage+FwMrh98WwhqLatlXu+z6pRJB0SF/y15A5zbAB7";
        String text =  "X4h4e/mjomrIkVqY3+N6VDR41f9beoBSqIz2wnxHeAdXl2beheBN0jZJk3t307kujfv4cTvUCYObQNxPUyq5OuuW3JO+sxjvBzBHT6dcMSCPi6h9w961hagknVJcUhage+FwMrh98WwhqLatlXu+z6pRJB0SF/y15A5zbAB7";
        System.out.println(decryptWithKeyStatic(text, encKey, iv));
    }
}
