package com.accionmfb.omnix.core.event;

import com.accionmfb.omnix.core.commons.ConfigSourceOperation;
import com.accionmfb.omnix.core.registry.LocalSourceCacheRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
@AutoConfiguration
@RequiredArgsConstructor
public class LocalCacheConfigEventHandler {

    @EventListener(value = ConfigSourcePropertyChangedEvent.class)
    public void handleConfigPropertyChangedEvent(ConfigSourcePropertyChangedEvent event){
        String paramKey = event.getParamKey();
        String paramValue = event.getParamValue();
        ConfigSourceOperation operation = event.getOperation();
        processConfigPropertySourceByOperation(paramKey, paramValue, operation);
    }

    private void processConfigPropertySourceByOperation(String paramKey, String paramValue, ConfigSourceOperation operation){
        switch (operation){
            case SAVE : {
                log.info("System discovers new config property with key: {} and value: {} inserted with SAVE operation in config database.", paramKey, paramValue);
                if(LocalSourceCacheRegistry.getUnmodifiableStartingKeys().contains(paramKey)){
                    log.info("System resolved that application needs configuration with this key. System will now go ahead to save this param to the application in-memory config source for use.");
                    LocalSourceCacheRegistry.setSource(paramKey, paramValue);
                }else{
                    log.info("System resolved that this new param is not needed by the running application and will thus, not be added to the in-memory config source");
                }
                break;
            }
            case UPDATE : {
                log.info("System discovers existing parameter with key: {} updated in database with UPDATE operation. System will now go ahead to update the configuration in the in-memory config source for subsequent application use", paramKey);
                LocalSourceCacheRegistry.setSource(paramKey, paramValue);
                break;
            }
            case DELETE : {
                log.info("System discovers a DELETE operation on config database for param with key: {}. System will now go ahead to delete it from the in-memory application config source", paramKey);
                LocalSourceCacheRegistry.removeEntry(paramKey);
                break;
            }
            default : log.info("System detected a {} operation on param in database. Unsupported operation detected. System will skip this operation and keep the in-memory application source intact.", operation);
        }
    }
}
