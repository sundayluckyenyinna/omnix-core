package com.accionmfb.omnix.core.event.handler;

import com.accionmfb.omnix.core.commons.ConfigSourceOperation;
import com.accionmfb.omnix.core.event.data.ConfigSourcePropertyChangedEvent;
import com.accionmfb.omnix.core.registry.LocalSourceCacheRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LocalCacheDeleteEventHandler implements LocalCacheEventHandler{
    @Override
    public boolean supportDBOperation(ConfigSourceOperation operation) {
        return operation == ConfigSourceOperation.DELETE;
    }

    @Override
    public void handleEvent(ConfigSourcePropertyChangedEvent event) {
        String paramKey = event.getParamKey();
        log.info("Omnix discovers a DELETE operation on config database for param with key: {}. System will now go ahead to delete it from the in-memory application config source", paramKey);
        LocalSourceCacheRegistry.removeEntry(paramKey);
    }
}
