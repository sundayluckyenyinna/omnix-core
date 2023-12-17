package com.accionmfb.omnix.core.service;

import java.util.List;
import java.util.Map;

public interface DatasourceService {
    Map<String, String> getOmnixParams(List<String> requiredParamKeys);

    <T> boolean saveOmnixParams(T paramKey, String paramValue);

    <T> boolean updateOmnixParam(T paramKey, String newParamValue);

    boolean deleteOmnixParam(String key);

    List<String> getLocalCacheParamKeys();

    List<String> getLocalCacheParamValues();

    String getParamValue(Object key);

    String getParamValue(String key);

    String getParamValueOrDefault(Object paramKey, String defaultValue, boolean createIfExist);

    String getParamValueOrDefault(Object paramKey, String defaultValue);
}
