package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.commons.StringValues;
import lombok.NonNull;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

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
}
