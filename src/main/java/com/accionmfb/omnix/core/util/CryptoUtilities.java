package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.commons.StringValues;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class CryptoUtilities {


    public static String base36Encode(byte[] input) {
        BigInteger number = new BigInteger(1, input);
        return number.toString(36).toUpperCase();
    }

    public static String base36Encode(String input){
        return base36Encode(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String base36Decode(String input){
        return new String(base36DecodeBytes(input));
    }

    private static byte[] base36DecodeBytes(String input) {
        BigInteger number = new BigInteger(input, 36);
        return number.toByteArray();
    }

    public static String sign(@NonNull String identity, @NonNull String secret){
        String concat = identity.concat(StringValues.COLON).concat(secret);
        return base36Encode(concat);
    }


    public static String generateMessageHash(Object payload, String channelId){
        Map<String, Object> pojoMap = CommonUtil.pojoToMap(payload, true);
        StringJoiner joiner = new StringJoiner(StringValues.COLON);
        pojoMap.forEach((key, value) -> {
            if(Objects.nonNull(value) && value.getClass().isAssignableFrom(String.class)){
                joiner.add(String.valueOf(value));
            }
        });
        String totalValue = joiner.toString().concat(StringValues.FORWARD_STROKE).concat(channelId);
        return CryptoUtilities.hash(totalValue, HashAlgorithm.SHA1.name().toUpperCase());
    }

    @SneakyThrows
    public static String hash(String value, String hashAlgorithm){
        MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm);
        byte[] hashedBytes = messageDigest.digest(value.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashedBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String hash(String value, HashAlgorithm hashAlgorithm){
        return hash(value, hashAlgorithm.getValue());
    }

    @Getter
    @RequiredArgsConstructor
    public enum HashAlgorithm{
        MD2("MD2"),
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        SHA384("SHA-384"),
        SHA512("SHA-512"),
        SHA3("SHA-3"),
        SHA224("SHA-224"),
        HMAC("HMAC"),
        RIPEMD160("RIPEMD160"),
        PBKDF2("PBKDF2");

        private final String value;
    }
}
