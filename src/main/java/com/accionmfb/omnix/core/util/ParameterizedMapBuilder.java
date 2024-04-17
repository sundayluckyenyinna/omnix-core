package com.accionmfb.omnix.core.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParameterizedMapBuilder<K, V> {

    private final Map<K, V> headerMap = new LinkedHashMap<>();

    public static <K, V> ParameterizedMapBuilder<K, V> withTypes(Class<K> kClass, Class<V> vClass){
        return new ParameterizedMapBuilder<>();
    }

    public ParameterizedMapBuilder<K, V> add(K key, V value){
        headerMap.put(key, value);
        return this;
    }

    public ParameterizedMapBuilder<K, V> addAll(Map<K, V> headers){
        headerMap.putAll(headers);
        return this;
    }

    public Map<K, V> asMap(){
        return this.headerMap;
    }
}
