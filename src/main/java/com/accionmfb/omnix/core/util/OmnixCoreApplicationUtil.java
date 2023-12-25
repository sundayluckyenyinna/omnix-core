package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.commons.StringValues;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
public class OmnixCoreApplicationUtil {

    public static String smartPrint(String prefix, String text, String suffix, int length){
        return prefix.concat(text).concat(write(StringValues.SINGLE_SPACE, length - text.length())).concat(suffix);
    }

    public static String write(String text, int times){
        return text.repeat(Math.max(0, times));
    }

    public static String writeInCenter(String text, int length){
        int rem = length - text.trim().length();
        int half = rem /2;
        return write(StringValues.SINGLE_SPACE, half).concat(text).concat(write(StringValues.SINGLE_SPACE, half));
    }

    public static boolean anyNull(Object ...objects){
        return Arrays.stream(objects).anyMatch(Objects::isNull);
    }

    public static <T> T returnOrdefault(T value, T defaultValue){
        return Objects.isNull(value) ? defaultValue : value;
    }

    public static LocalDateTime getCurrentTodayDateTime(){
        return LocalDateTime.now(ZoneId.of(StringValues.AFRICA_LAGOS_ZONE));
    }
}
