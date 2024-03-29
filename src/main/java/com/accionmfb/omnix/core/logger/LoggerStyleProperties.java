package com.accionmfb.omnix.core.logger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "logger.config.style")
public class LoggerStyleProperties {

    private String logStyle = "default";

}
