package com.accionmfb.omnix.core.logger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "logger.config.style")
public class LoggerStyleProperties {

    private String logStyle = "default";

}
