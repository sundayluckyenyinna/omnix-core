package com.accionmfb.omnix.core.localsource.core.impl;

import com.accionmfb.omnix.core.annotation.FallbackAdvice;
import com.accionmfb.omnix.core.annotation.FallbackHandler;
import com.accionmfb.omnix.core.annotation.RelaxAspectLogOperation;
import com.accionmfb.omnix.core.localsource.core.LocalParamStorage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Slf4j
@Primary
@RelaxAspectLogOperation
@RequiredArgsConstructor
@FallbackAdvice(value = SimpleLocalParamFallback.class)
public class DatasourceLocalParam implements LocalParamStorage {
    @Override
    @FallbackHandler(methodName = "getParamKeys", onValue = {"null"})
    public List<String> getParamKeys() {
        return null;
    }

    @Override
    @FallbackHandler(methodName = "getParamValues", onValue = "null")
    public List<String> getParamValues() {
        return null;
    }

    @Override
    @FallbackHandler(methodName = "getParamValue", onValue = { "null" })
    public String getParamValue(String paramKey) {
        return null;
    }

    @Override
    @FallbackHandler(methodName = "getParamValueWithObjectKey", onValue = { "null" })
    public String getParamValue(@NonNull Object enumParamKey) {
        return null;
    }

    @Override
    @FallbackHandler(methodName = "getParamValueOrDefaultCreateIfNotExist", onValue = { "null" })
    public String getParamValueOrDefault(Object paramKey, String defaultValue, boolean createIfExist) {
        return null;
    }

    @Override
    @FallbackHandler(methodName = "getParamValueOrDefault", onValue = { "null" })
    public String getParamValueOrDefault(Object paramKey, String defaultValue) {
        return null;
    }
}
