package com.accionmfb.omnix.core.localsource.core.impl;

import com.accionmfb.omnix.core.localsource.core.LocalApplicationStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleLocalApplicationStore implements LocalApplicationStore {

    private final static ConcurrentHashMap<String, Object> APPLICATION_STORE = new ConcurrentHashMap<>();

    @Override
    public void setItem(String key, Object value){
        if(isValidKeyPair(key, value)){
            APPLICATION_STORE.put(key, value);
        }
    }

    @Override
    public void setItem(Object key, Object value){
        setItem(String.valueOf(key), value);
    }

    @Override
    public Object getItem(String key){
        if(isValidKey(key)) {
            return APPLICATION_STORE.get(key);
        }
        return null;
    }

    @Override
    public Object getItem(Object key){
        return getItem(String.valueOf(key));
    }

    @Override
    public <T> T getItemCasted(String key, Class<T> tClass){
        return tClass.cast(getItem(key));
    }

    @Override
    public <T> T getItemCasted(Object key, Class<T> tClass){
        return getItemCasted(String.valueOf(key), tClass);
    }

    private static boolean isValidKeyPair(String key, Object value){
        return Objects.nonNull(key) && Objects.nonNull(value);
    }

    private static boolean isValidKey(String key){
        return Objects.nonNull(key);
    }
}
