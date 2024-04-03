package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.commons.StringValues;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class CommonUtil {

    public static LocalDateTime getCurrentDateTime(){
        return LocalDateTime.now(ZoneId.of(StringValues.AFRICA_LAGOS_ZONE));
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
}
