package com.accionmfb.omnix.core.localsource;

import com.accionmfb.omnix.core.localsource.config.SimpleLocalCacheConfigurer;
import com.accionmfb.omnix.core.localsource.core.impl.SimpleLocalParamCache;
import com.accionmfb.omnix.core.localsource.core.impl.SimpleLocalParamCacheFallback;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        SimpleLocalCacheConfigurer.class,
        SimpleLocalParamCache.class,
        SimpleLocalParamCacheFallback.class,
})
public class LocalCacheSourceModule {
}
