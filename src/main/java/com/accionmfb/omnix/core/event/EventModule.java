package com.accionmfb.omnix.core.event;

import com.accionmfb.omnix.core.event.handler.LocalCacheConfigEventHandler;
import com.accionmfb.omnix.core.event.handler.LocalCacheDeleteEventHandler;
import com.accionmfb.omnix.core.event.handler.LocalCacheSaveEventHandler;
import com.accionmfb.omnix.core.event.handler.LocalCacheUpdateEventHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        LocalCacheConfigEventHandler.class, LocalCacheDeleteEventHandler.class,
        LocalCacheSaveEventHandler.class, LocalCacheUpdateEventHandler.class
})
public class EventModule {
}
