package com.accionmfb.omnix.core.event.handler;

import com.accionmfb.omnix.core.commons.ConfigSourceOperation;
import com.accionmfb.omnix.core.event.data.ConfigSourcePropertyChangedEvent;

public interface LocalCacheEventHandler {

    boolean supportDBOperation(ConfigSourceOperation operation);
    void handleEvent(ConfigSourcePropertyChangedEvent event);
}
