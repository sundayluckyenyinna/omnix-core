package com.accionmfb.omnix.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        DatasourceConfig.class, UtilityConfig.class
})
public class ConfigModule {
}
