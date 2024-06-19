package com.accionmfb.omnix.core.localsource;

import com.accionmfb.omnix.core.localsource.config.SimpleLocalConfigurer;
import com.accionmfb.omnix.core.localsource.core.impl.DatasourceLocalParam;
import com.accionmfb.omnix.core.localsource.core.impl.SimpleLocalApplicationStore;
import com.accionmfb.omnix.core.localsource.core.impl.SimpleLocalParam;
import com.accionmfb.omnix.core.localsource.core.impl.SimpleLocalParamFallback;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        SimpleLocalConfigurer.class,
        SimpleLocalParam.class,
        DatasourceLocalParam.class,
        SimpleLocalApplicationStore.class,
        SimpleLocalParamFallback.class,
})
public class LocalSourceModule {
}
