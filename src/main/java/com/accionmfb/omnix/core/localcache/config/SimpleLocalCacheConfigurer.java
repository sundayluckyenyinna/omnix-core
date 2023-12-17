package com.accionmfb.omnix.core.localcache.config;

import com.accionmfb.omnix.core.annotation.RequiredOmnixParam;
import com.accionmfb.omnix.core.commons.SMART_PRINT_FORMAT;
import com.accionmfb.omnix.core.localcache.properties.LocalSourceCacheProperties;
import com.accionmfb.omnix.core.registry.LocalSourceCacheRegistry;
import com.accionmfb.omnix.core.service.DatasourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = LocalSourceCacheProperties.class)
public class SimpleLocalCacheConfigurer {

    private final DatasourceService datasourceService;
    private final Reflections reflections;
    private final LocalSourceCacheProperties localSourceCacheProperties;


    @Bean
    public String initParamSourceConfigurationFromDatabase(){
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RequiredOmnixParam.class);

        if(localSourceCacheProperties.isFetchOnStartup()) {
            for (Class<?> clazz : classes) {
                if (clazz.isEnum()) {
                    List<String> params = getEnumeratedParams(clazz);
                    Map<String, String> paramsMap = datasourceService.getOmnixParams(params);
                    LocalSourceCacheRegistry.setAllSource(paramsMap);
                    LocalSourceCacheRegistry.setConfigKeys(params);
                    log.info("Omnix Params registered to local cache successfully");
                }
            }
            LocalSourceCacheRegistry.setConfigKeys(LocalSourceCacheRegistry.getKeys());
        }
        if(localSourceCacheProperties.isEnableVerboseLogging()){
            LocalSourceCacheRegistry.printRegistry(getCacheLogFormat(localSourceCacheProperties.getLogFormat()), localSourceCacheProperties);
        }
        return "Success";
    }

    private List<String> getEnumeratedParams(Class<?> enumClass){
        return Arrays.stream(enumClass.getDeclaredFields())
                .filter(Objects::nonNull)
                .map(Field::getName)
                .filter(name -> !name.equalsIgnoreCase("$VALUES"))
                .collect(Collectors.toList());
    }

    private SMART_PRINT_FORMAT getCacheLogFormat(String formatStr){
        try {
            return SMART_PRINT_FORMAT.valueOf(formatStr.toUpperCase());
        }catch (Exception exception){
            return SMART_PRINT_FORMAT.LOGGING;
        }
    }
}
