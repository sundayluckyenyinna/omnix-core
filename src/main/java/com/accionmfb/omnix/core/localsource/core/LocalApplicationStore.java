package com.accionmfb.omnix.core.localsource.core;

public interface LocalApplicationStore {

    void setItem(String key, Object value);

    void setItem(Object key, Object value);

    Object getItem(String key);

    Object getItem(Object key);

    <T> T getItemCasted(String key, Class<T> tClass);

    <T> T getItemCasted(Object key, Class<T> tClass);
}
