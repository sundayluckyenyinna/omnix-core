package com.accionmfb.omnix.core.logger;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        OmnixHttpLogger.class,
        OmnixFeignLogger.class
})
public class LoggerModule {
}
