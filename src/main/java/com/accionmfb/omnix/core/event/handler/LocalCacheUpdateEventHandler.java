package com.accionmfb.omnix.core.event.handler;

import com.accionmfb.omnix.core.commons.ConfigSourceOperation;
import com.accionmfb.omnix.core.event.data.ConfigSourcePropertyChangedEvent;
import com.accionmfb.omnix.core.localsource.properties.LocalSourceProperties;
import com.accionmfb.omnix.core.registry.LocalSourceCacheRegistry;
import com.accionmfb.omnix.core.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(LocalSourceProperties.class)
public class LocalCacheUpdateEventHandler implements LocalCacheEventHandler{

    private final LocalSourceProperties properties;

    @Override
    public boolean supportDBOperation(ConfigSourceOperation operation) {
        return operation == ConfigSourceOperation.UPDATE;
    }

    @Override
    public void handleEvent(ConfigSourcePropertyChangedEvent event) {
        String paramKey = event.getParamKey();
        String paramValue = event.getParamValue();
        log.info("Omnix discovers existing parameter with key: {} updated in database with UPDATE operation. System will now go ahead to update the configuration in the in-memory config source for subsequent application use", paramKey);
        LocalSourceCacheRegistry.setSource(paramKey, paramValue);
        CommonUtil.runIf(properties.isLogOnUpdate(), () -> LocalSourceCacheRegistry.logRegistryConfig(properties));
    }
}
