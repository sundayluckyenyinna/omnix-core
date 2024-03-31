package com.accionmfb.omnix.core.localsource.core.impl;

import com.accionmfb.omnix.core.annotation.FallbackAdvice;
import com.accionmfb.omnix.core.annotation.FallbackHandler;
import com.accionmfb.omnix.core.annotation.RelaxAspectLogOperation;
import com.accionmfb.omnix.core.localsource.core.LocalParamCacheStorage;
import com.accionmfb.omnix.core.registry.LocalSourceCacheRegistry;
import com.accionmfb.omnix.core.service.DatasourceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;

@Configuration
@AutoConfiguration
@RelaxAspectLogOperation
@RequiredArgsConstructor
@FallbackAdvice(value = SimpleLocalParamCacheFallback.class)
public class SimpleLocalParamCache implements LocalParamCacheStorage {

    private final DatasourceService datasourceService;

    @Override
    @FallbackHandler(methodName = "getParamKeys", onValue = {"null"})
    public List<String> getParamKeys(){
        return LocalSourceCacheRegistry.getKeys();
    }

    @Override
    @FallbackHandler(methodName = "getParamValues", onValue = "null")
    public List<String> getParamValues(){
        return LocalSourceCacheRegistry.getValues();
    }

    @Override
    @FallbackHandler(methodName = "getParamValue", onValue = { "null" })
    public String getParamValue(String paramKey){
        return LocalSourceCacheRegistry.getValue(paramKey);
    }

    @Override
    @FallbackHandler(methodName = "getParamValueWithObjectKey", onValue = { "null" })
    public String getParamValue(@NonNull Object enumParamKey){
        return LocalSourceCacheRegistry.getValue(String.valueOf(enumParamKey));
    }

    @Override
    @FallbackHandler(methodName = "getParamValueOrDefaultCreateIfNotExist", onValue = { "null" })
    public String getParamValueOrDefault(Object paramKey, String defaultValue, boolean createIfExist){
        String value = LocalSourceCacheRegistry.getValue(String.valueOf(paramKey));
        if(Objects.isNull(value) && createIfExist){
            datasourceService.saveOmnixParams(paramKey, defaultValue);
            return defaultValue;
        }
        return Objects.isNull(value) ? defaultValue : value;
    }

    @Override
    @FallbackHandler(methodName = "getParamValueOrDefault", onValue = { "null" })
    public String getParamValueOrDefault(Object paramKey, String defaultValue){
        return getParamValueOrDefault(paramKey, defaultValue, true);
    }
}
