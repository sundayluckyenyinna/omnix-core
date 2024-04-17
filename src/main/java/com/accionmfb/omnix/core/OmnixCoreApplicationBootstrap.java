package com.accionmfb.omnix.core;

import com.accionmfb.omnix.core.advice.AdviceModule;
import com.accionmfb.omnix.core.aop.AopModule;
import com.accionmfb.omnix.core.config.ConfigModule;
import com.accionmfb.omnix.core.docs.SwaggerDocModule;
import com.accionmfb.omnix.core.encryption.EncryptionModule;
import com.accionmfb.omnix.core.event.EventModule;
import com.accionmfb.omnix.core.jwt.JwtModule;
import com.accionmfb.omnix.core.localsource.LocalSourceModule;
import com.accionmfb.omnix.core.logger.LoggerModule;
import com.accionmfb.omnix.core.logger.OmnixHttpLogger;
import com.accionmfb.omnix.core.service.ServiceModule;
import com.accionmfb.omnix.core.util.UtilModule;
import com.accionmfb.omnix.core.validation.ValidationModule;
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
        JwtModule.class,
        LocalSourceModule.class,
        LoggerModule.class,
        ServiceModule.class,
        UtilModule.class,
        ValidationModule.class
})
public class OmnixCoreApplicationBootstrap {
}
