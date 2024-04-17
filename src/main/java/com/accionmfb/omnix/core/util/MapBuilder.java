package com.accionmfb.omnix.core.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapBuilder
{
    private final Map<String, Object> headerMap = new LinkedHashMap<>();

    public static MapBuilder start(){
        return new MapBuilder();
    }

    public MapBuilder add(String key, Object value){
        headerMap.put(key, value);
        return this;
    }
    public MapBuilder addAll(Map<String, Object> headers){
        headerMap.putAll(headers);
        return this;
    }

    public Map<String, Object> asMap(){
        return this.headerMap;
    }
}
