package com.accionmfb.omnix.core.docs;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ SwaggerDocsConfig.class })
public class SwaggerDocModule {
}
