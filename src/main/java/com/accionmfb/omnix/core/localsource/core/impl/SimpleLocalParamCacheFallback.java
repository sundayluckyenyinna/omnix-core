package com.accionmfb.omnix.core.localsource.core.impl;

import com.accionmfb.omnix.core.annotation.FallbackParam;
import com.accionmfb.omnix.core.service.DatasourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@AutoConfiguration
@RequiredArgsConstructor
public class SimpleLocalParamCacheFallback {

    private final DatasourceService datasourceService;

    public List<String> getParamKeys(){
        return datasourceService.getLocalCacheParamKeys();
    }

    public List<String> getParamValues(){
        return datasourceService.getLocalCacheParamValues();
    }

    public String getParamValue(@FallbackParam("paramKey") String key){
        return datasourceService.getParamValue(key);
    }

    public String getParamValueWithObjectKey(@FallbackParam("enumParamKey") Object key){
        return datasourceService.getParamValue(key);
    }

    public String getParamValueOrDefaultCreateIfNotExist(@FallbackParam("paramKey") Object paramKey,
                                                         @FallbackParam("defaultValue") String defaultValue,
                                                         @FallbackParam("createIfExist") boolean createIfExist){
        return datasourceService.getParamValueOrDefault(paramKey, defaultValue, createIfExist);
    }

    public String getParamValueOrDefault(@FallbackParam("paramKey") Object paramKey,
                                         @FallbackParam("defaultValue") String defaultValue){
        return datasourceService.getParamValueOrDefault(paramKey, defaultValue);
    }
}
