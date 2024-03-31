package com.accionmfb.omnix.core.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        JdbcDatasourceServiceImpl.class
})
public class ServiceModule {
}
