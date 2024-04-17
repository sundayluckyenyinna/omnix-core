package com.accionmfb.omnix.core.docs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "docs.swagger")
public class SwaggerDocProperties {
    private String email = "noreply@accionmfb.com";
    private String name = "Micro - Service";
    private String url = "https://www.swagger-docs.com";
    private String title = "Micro - Service API";
    private String version = "1.0";
    private String description = "Central documentation for micro-service api";
    private String controllerPackage;
}
