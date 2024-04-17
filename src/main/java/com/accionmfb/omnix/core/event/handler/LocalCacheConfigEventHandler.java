package com.accionmfb.omnix.core.event.handler;

import com.accionmfb.omnix.core.event.data.ConfigSourcePropertyChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.Objects;

@Slf4j
@Configuration

@RequiredArgsConstructor
public class LocalCacheConfigEventHandler {

    private final List<LocalCacheEventHandler> handlers;

    @EventListener(value = ConfigSourcePropertyChangedEvent.class)
    public void handleConfigPropertyChangedEvent(ConfigSourcePropertyChangedEvent event){
        processConfigPropertySourceByOperation(event);
    }

    private void processConfigPropertySourceByOperation(ConfigSourcePropertyChangedEvent event){
        LocalCacheEventHandler handler = handlers.stream().filter(localCacheEventHandler -> localCacheEventHandler.supportDBOperation(event.getOperation())).findFirst().orElse(null);
        if(Objects.nonNull(handler)){
            handler.handleEvent(event);
        }else {
            log.info("Omnix detected a {} operation on param in database. Unsupported operation detected. System will skip this operation and keep the in-memory application source intact.", event.getOperation());
        }
    }
}
