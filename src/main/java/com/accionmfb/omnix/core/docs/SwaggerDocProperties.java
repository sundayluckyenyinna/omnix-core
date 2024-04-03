package com.accionmfb.omnix.core.docs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "docs.swagger")
public class SwaggerDocProperties {
    private String email = "noreply@accionmfb.com";
    private String name = "Micro - Service";
    private String url = "";
    private String title = "";
    private String version = "1.0";
    private String description = "";
}
