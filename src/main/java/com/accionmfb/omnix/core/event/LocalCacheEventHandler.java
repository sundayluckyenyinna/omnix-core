package com.accionmfb.omnix.core.event;

import com.accionmfb.omnix.core.commons.ConfigSourceOperation;

public interface LocalCacheEventHandler {

    boolean supportDBOperation(ConfigSourceOperation operation);
    void handleEvent(ConfigSourcePropertyChangedEvent event);
}
