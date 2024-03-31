package com.accionmfb.omnix.core.aop;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ OmnixCoreMethodProxyInterceptor.class })
public class AopModule {
}
