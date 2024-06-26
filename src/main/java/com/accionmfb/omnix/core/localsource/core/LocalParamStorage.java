package com.accionmfb.omnix.core.localsource.core;

import lombok.NonNull;

import java.util.List;

public interface LocalParamStorage {
    List<String> getParamKeys();

    List<String> getParamValues();

    String getParamValue(String paramKey);

    String getParamValue(@NonNull Object enumParamKey);

    String getParamValueOrDefault(Object paramKey, String defaultValue, boolean createIfExist);

    String getParamValueOrDefault(Object paramKey, String defaultValue);
}
