package com.accionmfb.omnix.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GlobalContextStore {

    private static final ConcurrentHashMap<String, Object> contextStore = new ConcurrentHashMap<>();

    public static void save(String key, Object value) {
        validateContextKey(key);
        contextStore.put(key, value);
    }

    public static Object get(String key) {
        validateContextKey(key);
        return contextStore.get(key);
    }

    public static <T> T get(String key, Class<T> tClass) {
        Object value = get(key);
        return Objects.isNull(value) ? null : tClass.cast(value);
    }

    public static Object getOrSupply(String key, Supplier<Object> objectSupplier) {
        validateValueSupplier(objectSupplier);
        Object value = get(key);
        return CommonUtil.returnOrDefault(value, objectSupplier.get());
    }

    public static Object getOrSupplyAndSet(String key, Supplier<Object> objectSupplier) {
        validateValueSupplier(objectSupplier);
        Object value = get(key);
        saveValueFromSupplierIfAbsent(key, value, objectSupplier);
        return value;
    }

    public static <T> T getOrSupply(String key, Class<T> tClass, Supplier<T> valueSupplier) {
        validateValueSupplier(valueSupplier);
        T value = get(key, tClass);
        return CommonUtil.returnOrDefault(value, valueSupplier.get());
    }

    public static <T> T getOrSupplyAndSet(String key, Class<T> tClass, Supplier<T> objectSupplier) {
        validateValueSupplier(objectSupplier);
        T value = get(key, tClass);
        saveValueFromSupplierIfAbsent(key, value, objectSupplier);
        return value;
    }

    public static Object unlink(String key) {
        validateContextKey(key);
        return contextStore.remove(key);
    }

    public static <T> T unlink(String key, Class<T> tClass) {
        validateContextKey(key);
        Object value = unlink(key);
        return Objects.isNull(value) ? null : tClass.cast(value);
    }

    public static List<String> keys() {
        return Collections.list(contextStore.keys());
    }

    public static List<Object> values() {
        return new ArrayList<>(contextStore.values());
    }

    public static <T> List<T> values(Class<T> tClass) {
        return values().stream().map(tClass::cast).collect(Collectors.toList());
    }

    private static void saveValueFromSupplierIfAbsent(
            String key, Object value, Supplier<?> objectSupplier) {
        if (Objects.isNull(value)) {
            value = objectSupplier.get();
            if (Objects.nonNull(value)) {
                save(key, value);
            }
        }
    }

    private static void validateContextKey(String key) {
        if (Objects.isNull(key)) {
            throw new IllegalArgumentException("Context key cannot be null");
        }
    }

    private static void validateValueSupplier(Supplier<?> valueSupplier) {
        if (Objects.isNull(valueSupplier)) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
    }
}
