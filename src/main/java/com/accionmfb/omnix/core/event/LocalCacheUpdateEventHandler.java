package com.accionmfb.omnix.core.event;

import com.accionmfb.omnix.core.commons.ConfigSourceOperation;
import com.accionmfb.omnix.core.registry.LocalSourceCacheRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LocalCacheUpdateEventHandler implements LocalCacheEventHandler{
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
    }
}
