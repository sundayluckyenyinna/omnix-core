package com.accionmfb.omnix.core.jwt;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration

@Import({
        JwtTokenUtility.class
})
public class JwtModule {
}
