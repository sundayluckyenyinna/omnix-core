package com.accionmfb.omnix.core;

import com.accionmfb.omnix.core.advice.AdviceModule;
import com.accionmfb.omnix.core.aop.AopModule;
import com.accionmfb.omnix.core.config.ConfigModule;
import com.accionmfb.omnix.core.docs.SwaggerDocModule;
import com.accionmfb.omnix.core.encryption.EncryptionModule;
import com.accionmfb.omnix.core.event.EventModule;
import com.accionmfb.omnix.core.localsource.LocalCacheSourceModule;
import com.accionmfb.omnix.core.logger.OmnixHttpLogger;
import com.accionmfb.omnix.core.service.ServiceModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        AdviceModule.class,
        AopModule.class,
        ConfigModule.class,
        SwaggerDocModule.class,
        EncryptionModule.class,
        EventModule.class,
        LocalCacheSourceModule.class,
        OmnixHttpLogger.class,
        ServiceModule.class
})
public class OmnixCoreApplicationBootstrap {
}
