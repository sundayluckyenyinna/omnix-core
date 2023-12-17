package com.accionmfb.omnix.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AutoConfiguration
@RequiredArgsConstructor
public class OmnixCoreErrorCapture {

    @Bean
    public String omnixCoreCaptureErrorOnStartup(){
        log.info("");
        log.info("---------------------------------------------------");
        log.info("| Omnix - Core initialized successfully           |");
        log.info("---------------------------------------------------");
        return "Success";
    }
}
