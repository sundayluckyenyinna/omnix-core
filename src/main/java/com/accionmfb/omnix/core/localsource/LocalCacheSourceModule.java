package com.accionmfb.omnix.core.localsource;

import com.accionmfb.omnix.core.localsource.config.SimpleLocalCacheConfigurer;
import com.accionmfb.omnix.core.localsource.core.impl.SimpleLocalParamCache;
import com.accionmfb.omnix.core.localsource.core.impl.SimpleLocalParamCacheFallback;
import com.accionmfb.omnix.core.localsource.http.LocalCacheUpdateController;
import com.accionmfb.omnix.core.localsource.http.LocalCacheUpdateService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        SimpleLocalCacheConfigurer.class, SimpleLocalParamCache.class,
        SimpleLocalParamCacheFallback.class, LocalCacheUpdateController.class,
        LocalCacheUpdateService.class
})
public class LocalCacheSourceModule {
}
