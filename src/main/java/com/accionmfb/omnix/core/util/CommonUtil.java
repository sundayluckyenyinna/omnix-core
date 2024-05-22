package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.commons.ResponseCode;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.exception.OmnixApiException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;

@Component
@RequiredArgsConstructor
public class CommonUtil {

    private final static ObjectMapper C_OBJECT_MAPPER = new ObjectMapper();
    private final MessageSource messageSource;
    private final static String ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String NIGERIAN_PHONE_PREFIX_CODE = "234";
    private final static String NIGERIAN_PHONE_PREFIX_POSITIVE_CODE = "+234";
    private final static String NIGERIAN_PHONE_PREFIX_POSITIVE_CODE_ESC = "\\+234";
    private final static String NIGERIAN_MOBILE_PREFIX_NO_CODE = "0";

    private final static List<String> MTN_PREFIX = List.of(
            "0803", "0816", "0903", "0810",
            "0806", "0703", "0706", "0813",
            "0814", "0906"
    );

    private final static List<String> AIRTEL_PREFIX = List.of(
            "0907", "0708", "0802", "0902",
            "0812", "0808", "0701"
    );

    private final static List<String> GLO_PREFIX = List.of(
            "0805", "0905", "0807", "0811",
            "0705", "0815"
    );

    private final static List<String> NINE_MOBILE_PREFIX = List.of(
            "0909", "0908", "0818", "0809",
            "0817"
    );

    private static final DateTimeFormatter AM_PM_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    public static ZoneId getLagosZoneId(){
        return ZoneId.of(StringValues.AFRICA_LAGOS_ZONE);
    }

    public static LocalDateTime getCurrentDateTime(){
        return LocalDateTime.now(getLagosZoneId());
    }

    public static LocalDate getCurrentLocalDate(){
        return getCurrentDateTime().toLocalDate();
    }

    public static void runIf(boolean condition, OmnixOperation operation){
        if(condition & Objects.nonNull(operation)){
            operation.execute();
        }
    }

    public static <T> void runIf(boolean condition, Consumer<T> consumer, T args){
        if(condition & Objects.nonNull(consumer)){
            consumer.accept(args);
        }
    }

    public static <T, U> void runIf(boolean condition, BiConsumer<T, U> biConsumer, T firstArg, U secondArg){
        if(condition && Objects.nonNull(biConsumer)){
            biConsumer.accept(firstArg, secondArg);
        }
    }

    public static <T, R> R produceIf(boolean condition, Function<T, R> function, T args){
        if(condition && Objects.nonNull(function)){
            return function.apply(args);
        }
        return null;
    }

    public static <T> T copyProperties(Object source, T target){
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static String cleanToken(String token){
        token = Objects.isNull(token) ? StringValues.EMPTY_STRING : token;
        return token.startsWith(StringValues.BEARER_PREFIX) ? token.replace(StringValues.BEARER_PREFIX, StringValues.EMPTY_STRING).trim() : token.trim();
    }

    public static String encodeToBase64(String str){
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeToBase64(String str){
        return new String(Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8)));
    }


    public static <T> T returnOrDefault(T value, T defaultValue){
        return Objects.isNull(value) ? defaultValue : value;
    }

    public static boolean isNullOrEmpty(Object object){
        return Objects.isNull(object) || String.valueOf(object).isEmpty();
    }

    public static boolean nonNullOrEmpty(Object object){
        return !isNullOrEmpty(object);
    }

    public static int generateRandomInt(int upperbound){
        return new Random().nextInt(upperbound);
    }
    public static String generateRandomAlphabet(int max){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < max; i++){
            builder.append(ALPHABETS.charAt(generateRandomInt(26)));
        }
        return builder.toString();
    }

    public static Map<String, Object> pojoToMap(Object pojo, boolean excludeNull){
        Map<String, Object> map = new LinkedHashMap<>();
        Arrays.stream(pojo.getClass().getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        Object fieldValue = field.get(pojo);
                        if(Objects.isNull(fieldValue)){
                            if(!excludeNull){
                                map.put(field.getName(), null);
                            }
                        }else{
                            map.put(field.getName(), fieldValue);
                        }
                    } catch (IllegalAccessException ignored) {}
                });
        return map;
    }

    public static Map<String, Object> pojoToMap(Object pojo){
        return pojoToMap(pojo, true);
    }

    public static String generateRandomIntString(int length){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < length; i++){
            builder.append(generateRandomInt(10));
        }
        return builder.toString();
    }

    public static String formatToOmnixNigerianMobileNumber(String phoneNumber){
        if(Objects.isNull(phoneNumber)){
            return null;
        }
        phoneNumber = phoneNumber.trim();
        if(phoneNumber.length() >= 13){
            String escapedReplacement = Matcher.quoteReplacement(NIGERIAN_MOBILE_PREFIX_NO_CODE);
            if(phoneNumber.startsWith(NIGERIAN_PHONE_PREFIX_POSITIVE_CODE)){
                return phoneNumber.replaceFirst(NIGERIAN_PHONE_PREFIX_POSITIVE_CODE_ESC, escapedReplacement);
            }
            if(phoneNumber.startsWith(NIGERIAN_PHONE_PREFIX_CODE)){
                return phoneNumber.replaceFirst(NIGERIAN_PHONE_PREFIX_CODE, escapedReplacement);
            }
        }
        return phoneNumber.trim().length() == 10 ? NIGERIAN_MOBILE_PREFIX_NO_CODE.concat(phoneNumber) : phoneNumber ;
    }

    public static String formatMobileWithNigerianCode(String mobile){
        String formattedMobile;
        if(mobile.startsWith(NIGERIAN_PHONE_PREFIX_CODE)){
            formattedMobile = mobile.trim();
        }else{
            formattedMobile = NIGERIAN_PHONE_PREFIX_CODE.concat(mobile.substring(1)).trim();
        }
        return formattedMobile;
    }

    public static String resolveTelcoFromMobileNumber(String mobileNumber){
        String formattedMobile = formatToOmnixNigerianMobileNumber(mobileNumber);
        assert formattedMobile != null;
        String prefix = formattedMobile.substring(0, 4);
        if(MTN_PREFIX.contains(prefix)){
            return "MTN";
        }
        if(AIRTEL_PREFIX.contains(prefix)){
            return "AIRTEL";
        }
        if(GLO_PREFIX.contains(prefix)){
            return "GLO";
        }
        if(NINE_MOBILE_PREFIX.contains(prefix)){
            return "NINE_MOBILE";
        }
        return "UNKNOWN";
    }

    public static String prettyPrint(Object object){
        try {
            C_OBJECT_MAPPER.registerModule(new JavaTimeModule());
            C_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            C_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            C_OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return C_OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        }catch (Exception ignored){}
        return StringValues.EMPTY_STRING;
    }

    public static String maskString(String input, int visibleCharacters) {
        if (input == null || input.isEmpty() || visibleCharacters >= input.length()) {
            return input;
        }
        StringBuilder maskedString = new StringBuilder();
        int maskedLength = input.length() - visibleCharacters;
        maskedString.append(input, 0, visibleCharacters);
        maskedString.append("x".repeat(Math.max(0, maskedLength)));
        maskedString.append(input, input.length() - visibleCharacters, input.length());
        return maskedString.toString();
    }

    //--------------------------------------- List Utils -------------------------------//
    public static <T> List<T> merge(List<T> first, List<T> second){
        List<T> combined = new ArrayList<>();
        combined.addAll(first);
        combined.addAll(second);
        return combined;
    }

    public static String formatToAmPmTime(LocalTime localTime){
        return AM_PM_TIME_FORMATTER.format(localTime);
    }

    public static void throwInternalServerError(Exception exception){
        throw OmnixApiException.newInstance()
                .withCode(ResponseCode.INTERNAL_SERVER_ERROR)
                .withMessage("Internal server error has occurred")
                .withError("Internal server error has occurred")
                .withException(exception);
    }

    // -------------------------------- Message source util ---------------------------//
    public String getMessage(String key){
        try {
            return messageSource.getMessage(key, new Object[]{}, Locale.ENGLISH);
        }catch (Exception exception){
            return key;
        }
    }

    public String getMessage(String key, Object[] params){
        try {
            return messageSource.getMessage(key, params, Locale.ENGLISH);
        }catch (Exception exception){
            return  key;
        }
    }
}
