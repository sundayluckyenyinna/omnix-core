package com.accionmfb.omnix.core.event;

import com.accionmfb.omnix.core.commons.ConfigSourceOperation;
import com.accionmfb.omnix.core.registry.LocalSourceCacheRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LocalCacheSaveEventHandler implements LocalCacheEventHandler{
    @Override
    public boolean supportDBOperation(ConfigSourceOperation operation) {
        return operation == ConfigSourceOperation.SAVE;
    }

    @Override
    public void handleEvent(ConfigSourcePropertyChangedEvent event) {
        String paramKey = event.getParamKey();
        String paramValue = event.getParamValue();
        log.info("Omnix discovers new config property with key: {} and value: {} inserted with SAVE operation in config database.", paramKey, paramValue);
        if(LocalSourceCacheRegistry.getUnmodifiableStartingKeys().contains(paramKey)){
            log.info("Omnix resolved that application needs configuration with this key. System will now go ahead to save this param to the application in-memory config source for use.");
            LocalSourceCacheRegistry.setSource(paramKey, paramValue);
        }else{
            log.info("Omnix resolved that this new param is not needed by the running application and will thus, not be added to the in-memory config source");
        }
    }
}
