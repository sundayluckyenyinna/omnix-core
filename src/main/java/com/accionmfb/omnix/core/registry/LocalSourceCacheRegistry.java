package com.accionmfb.omnix.core.registry;

import com.accionmfb.omnix.core.commons.SMART_PRINT_FORMAT;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.localcache.properties.LocalSourceCacheProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.accionmfb.omnix.core.util.OmnixCoreApplicationUtil.*;

@Slf4j
public class LocalSourceCacheRegistry {

    private static final ConcurrentMap<String, String> LOCAL_SOURCE_CACHE = new ConcurrentHashMap<>();
    private static final List<String> CONFIG_KEYS = new ArrayList<>();

    public static void setConfigKeys(List<String> keys){
        CONFIG_KEYS.addAll(keys);
    }

    public static List<String> getUnmodifiableStartingKeys(){
        return CONFIG_KEYS;
    }

    public static void setSource(String key, String value){
        if(Objects.nonNull(key) && Objects.nonNull(value)){
            LOCAL_SOURCE_CACHE.put(key, value);
        }
    }

    public static Map<String, String> getConfigurationMap(){
        return LOCAL_SOURCE_CACHE;
    }

    public static void setAllSource(Map<String, String> maps){
        for(Map.Entry<String, String> entry : maps.entrySet()){
            setSource(entry.getKey(), entry.getValue());
        }
    }

    public static String getValue(String key){
        if(Objects.nonNull(key)){
            return LOCAL_SOURCE_CACHE.get(key);
        }
        return null;
    }

    public static List<String> getKeys(){
        return new ArrayList<>(LOCAL_SOURCE_CACHE.keySet());
    }

    public static List<String> getValues(){
        return new ArrayList<>(LOCAL_SOURCE_CACHE.values());
    }

    public static boolean removeEntry(String key){
        LOCAL_SOURCE_CACHE.remove(key);
        return true;
    }

    @SneakyThrows
    public static void printRegistry(SMART_PRINT_FORMAT format, LocalSourceCacheProperties properties){
        if(format == SMART_PRINT_FORMAT.LOGGING){
            logRegistryConfig(properties);
        } else if (format == SMART_PRINT_FORMAT.SYSTEM_DEFAULT) {
            printRegistryConfig(properties);
        }
        else if(format == SMART_PRINT_FORMAT.LOG_AND_PRINT){
            logRegistryConfig(properties);
            printRegistryConfig(properties);
        }
        else if(format == SMART_PRINT_FORMAT.PRINT_AND_LOG){
            printRegistryConfig(properties);
            logRegistryConfig(properties);
        }
        else {
            logRegistryConfig(properties);
        }
    }

    public static void logRegistryConfig(LocalSourceCacheProperties properties){

        log.info("");
        log.info("OMNIX PARAMS CACHE CONFIGURATIONS");
        log.info(write("-", 70));
        getMetaData(properties).forEach(data -> log.info(smartPrint("| ", data, " |", 66)));
        log.info(write("-", 70));

        Object[] objects = getConfigurationsKeyValuePair();
        List<String> pairs = (List<String>) objects[0];
        int maxLength = (int) objects[1];
        boolean shouldPrint = (boolean) objects[2];
        int pad = maxLength + 3;
        AtomicInteger atomicInteger = new AtomicInteger(1);

        log.info("");
        log.info(writeInCenter("CONFIGURATIONS", pad));
        log.info(write("-", pad));
        pairs.forEach(s -> {
            log.info(smartPrint("| ", s, "|", 0));
            if(atomicInteger.get() == 1){
                log.info(write("-", pad));
            }
            atomicInteger.incrementAndGet();
        });

        if(shouldPrint) {
            log.info(write("-", pad));
            log.info("");
        }else{
            log.info("");
        }
    }

    public static void printRegistryConfig(LocalSourceCacheProperties properties){

        System.out.println();
        System.out.println("OMNIX PARAMS CACHE CONFIGURATIONS");
        System.out.println(write("-", 69));
        getMetaData(properties).forEach(data -> System.out.println(smartPrint("| ", data, "|", 66)));
        System.out.println(write("-", 69));

        Object[] objects = getConfigurationsKeyValuePair();
        List<String> pairs = (List<String>) objects[0];
        int maxLength = (int) objects[1];
        boolean shouldPrint = (boolean) objects[2];
        int pad = maxLength + 3;
        AtomicInteger atomicInteger = new AtomicInteger(1);

        System.out.println();
        System.out.println(writeInCenter("CONFIGURATIONS", pad));
        System.out.println(write("-", pad));
        pairs.forEach(s -> {
            System.out.println(smartPrint("| ", s, "|", 0));
            if(atomicInteger.get() == 1){
                System.out.println(write("-", pad));
            }
            atomicInteger.incrementAndGet();
        });

        if(shouldPrint) {
            System.out.println(write("-", pad));
            System.out.println();
        }else {
            System.out.println();
        }
    }

    private static List<String> getMetaData(LocalSourceCacheProperties properties){
        return Arrays.asList(
                "Table Name: " + properties.getSourceTableName(),
                "Param Keys Column Name: " + properties.getParamKeyColumnName(),
                "Param Values Column Name: " + properties.getParamValueColumnName()
        );
    }

    private static Object[] getConfigurationsKeyValuePair(){
        final String padding = write(StringValues.SINGLE_SPACE, 2);
        List<String> keys = new ArrayList<>(LOCAL_SOURCE_CACHE.keySet());
        List<String> values = new ArrayList<>(LOCAL_SOURCE_CACHE.values());
        int maxStringKeyLen = Math.max(keys.stream().max(Comparator.comparingInt(String::length)).orElse("").length(), 31);
        int maxStringValueLen = Math.max(values.stream().max(Comparator.comparing(String::length)).orElse(StringValues.EMPTY_STRING).length(), 31);
        List<String> keyValuePair = new ArrayList<>();
        LOCAL_SOURCE_CACHE.forEach((key, value) -> keyValuePair.add(smartPrint("", key, "", maxStringKeyLen) + padding + "| " + smartPrint("", value, " ", maxStringValueLen)));
        keyValuePair.add(0, smartPrint("", "Key", "", maxStringKeyLen) + padding + "| " + smartPrint("", "Value", " ", maxStringValueLen));
        return new Object[]{keyValuePair, keyValuePair.get(0).length(), keyValuePair.size() > 1};
    }
}
