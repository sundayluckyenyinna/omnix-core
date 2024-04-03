package com.accionmfb.omnix.core.docs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = SwaggerDocProperties.class)
public class SwaggerDocsConfig {

    private final SwaggerDocProperties swaggerDocProperties;

    @Bean
    public OpenAPI openAPI() {
        Contact contact = new Contact();
        contact.setEmail(swaggerDocProperties.getEmail());
        contact.setName(swaggerDocProperties.getName());
        contact.setUrl(swaggerDocProperties.getUrl());

        Info info = new Info()
                .title(swaggerDocProperties.getTitle())
                .version(swaggerDocProperties.getVersion())
                .contact(contact)
                .description(swaggerDocProperties.getDescription());

        return new OpenAPI().info(info);
    }
}
