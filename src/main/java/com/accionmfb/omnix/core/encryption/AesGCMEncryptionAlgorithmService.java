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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
            byte[] keyBytes = encKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Generate a 12-byte IV for GCM
            byte[] iv = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv); // 128-bit tag

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            byte[] plainText = stringToEncrypt.getBytes(StandardCharsets.UTF_8);
            byte[] cipherTextWithTag = cipher.doFinal(plainText);

            System.out.println("Cypher text with tag: ============>" + Base64.getEncoder().encodeToString(cipherTextWithTag));

            // Separate cipherText and tag
            int tagLength = 16; // 128 bits
            int ctLength = cipherTextWithTag.length - tagLength;

            byte[] tag = Arrays.copyOfRange(cipherTextWithTag, ctLength, cipherTextWithTag.length);

            // Encode everything
            String base64CipherText = Base64.getEncoder().encodeToString(cipherTextWithTag);
            String base64IV         = Base64.getEncoder().encodeToString(iv);
            String base64Tag        = Base64.getEncoder().encodeToString(tag);

            // Set IV and TAG in response headers
            httpServletResponse.setHeader(IV_PARAMETER_KEY, base64IV);
            httpServletResponse.setHeader(AUTH_TAG_PARAMETER_KEY, base64Tag);

            return base64CipherText;

        } catch (Exception ex) {
            log.error("AES-GCM encryption failed: {}", ex.getMessage(), ex);
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
            Cipher cipher = Cipher.getInstance(CommonUtil.returnOrDefault(cipherInstance, "AES/GCM/NoPadding"));

            // Get IV and tag from headers
            String base64IV = httpServletRequest.getHeader(IV_PARAMETER_KEY);
            String base64Tag = httpServletRequest.getHeader(AUTH_TAG_PARAMETER_KEY);

            if (base64IV == null || base64Tag == null) {
                log.error("Missing IV or Auth Tag in request headers");
                return null;
            }

            byte[] iv = Base64.getDecoder().decode(base64IV);
            byte[] tag = Base64.getDecoder().decode(base64Tag);

            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            byte[] cipherText = Base64.getDecoder().decode(stringToDecrypt);

            // Reconstruct cipherTextWithTag: ciphertext || tag
            byte[] cipherTextWithTag = new byte[cipherText.length + tag.length];
            System.arraycopy(cipherText, 0, cipherTextWithTag, 0, cipherText.length);
            System.arraycopy(tag, 0, cipherTextWithTag, cipherText.length, tag.length);

            byte[] plainText = cipher.doFinal(cipherTextWithTag);
            return new String(plainText, StandardCharsets.UTF_8);

        } catch (Exception ex) {
            log.error("AES-GCM decryption failed: {}", ex.getMessage(), ex);
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
        return algorithm.equalsIgnoreCase(EncryptionAlgorithm.AES_GCM.name());
    }


    public static String encryptWithKeyStatic(String stringToEncrypt, String encKey) {
        try {
            byte[] keyBytes = encKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Generate a 12-byte IV for GCM
            byte[] iv = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv); // 128-bit tag

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            byte[] plainText = stringToEncrypt.getBytes(StandardCharsets.UTF_8);
            byte[] cipherTextWithTag = cipher.doFinal(plainText);

            System.out.println("Cypher text with tag: ============>" + Base64.getEncoder().encodeToString(cipherTextWithTag));

            // Separate cipherText and tag
            int tagLength = 16; // 128 bits
            int ctLength = cipherTextWithTag.length - tagLength;

            byte[] tag = Arrays.copyOfRange(cipherTextWithTag, ctLength, cipherTextWithTag.length);

            // Encode everything
            String base64CipherText = Base64.getEncoder().encodeToString(cipherTextWithTag);
            String base64IV         = Base64.getEncoder().encodeToString(iv);
            String base64Tag        = Base64.getEncoder().encodeToString(tag);

            return base64CipherText;

        } catch (Exception ex) {
            log.error("AES-GCM encryption failed: {}", ex.getMessage(), ex);
            return null;
        }
    }

    public static String decryptWithKeyStatic(String stringToDecrypt, String encKey) {
        try {
            byte[] key = encKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Get IV and tag from headers
            String base64IV = "yDrN+1Z4rgXxI+1l";

            byte[] iv = Base64.getDecoder().decode(base64IV);
            byte[] tag = Base64.getDecoder().decode("lxebf+Pm4X6XqcUxOwNLDg==");

            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            byte[] cipherText = Base64.getDecoder().decode(stringToDecrypt);

            // Reconstruct cipherTextWithTag: ciphertext || tag
            byte[] cipherTextWithTag = new byte[cipherText.length + tag.length];
            System.arraycopy(cipherText, 0, cipherTextWithTag, 0, cipherText.length);
            System.arraycopy(tag, 0, cipherTextWithTag, cipherText.length, tag.length);

            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);

        } catch (Exception ex) {
            log.error("AES-GCM decryption failed: {}", ex.getMessage(), ex);
        }
        return null;
    }

    public static String decr(String string, String ivStr) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] keyBytes = "77T18925x42783H7508302949Q618671".getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        byte[] iv = Base64.getDecoder().decode(ivStr);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv); // 128-bit tag length

        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

        byte[] cypherText = Base64.getDecoder().decode(string);
        byte[] plainTextBytes = cipher.doFinal(cypherText);
        String decryptedText = new String(plainTextBytes, StandardCharsets.UTF_8);

        System.out.println("Decrypted: " + decryptedText);
        return decryptedText;
    }

    public static String decryptFromFE(String cipherTextBase64, String ivBase64, String tagBase64) throws Exception {
        byte[] keyBytes = "77T18925x42783H7508302949Q618671".getBytes(StandardCharsets.UTF_8); // 32 bytes = AES-256
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        byte[] iv = Base64.getDecoder().decode(ivBase64);
        byte[] cipherText = Base64.getDecoder().decode(cipherTextBase64);
        byte[] tag = Base64.getDecoder().decode(tagBase64);

        // Combine cipherText + tag
        byte[] cipherTextWithTag = new byte[cipherText.length + tag.length];
        System.arraycopy(cipherText, 0, cipherTextWithTag, 0, cipherText.length);
        System.arraycopy(tag, 0, cipherTextWithTag, cipherText.length, tag.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv); // 128-bit tag

        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        byte[] plainTextBytes = cipher.doFinal(cipherTextWithTag);

        return new String(plainTextBytes, StandardCharsets.UTF_8);
    }


    public static void main(String[] args) throws Exception {
        System.out.println(decryptFromFE("DF4pMdRnEKaGvJJtUx7yUXlLVJEQhGdbbyI1dOdubQyjhRyqjhnZWkJc1G7DYdAaUtHFm6EV+JWTMpkaAHSi5PckN0s+Zfb4kdsM8ASwAGo=", "yDrN+1Z4rgXxI+1l", "lxebf+Pm4X6XqcUxOwNLDg=="));
    }

}
