package com.accionmfb.omnix.core.docs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Configuration

@RequiredArgsConstructor
@EnableConfigurationProperties(value = SwaggerDocProperties.class)
public class SwaggerDocsConfig {

    private final SwaggerDocProperties swaggerDocProperties;

    @Bean
    public Docket api() {
        boolean basePackageSet = Objects.nonNull(swaggerDocProperties.getControllerPackage());
        String basePackages = swaggerDocProperties.getControllerPackage();
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(basePackageSet ? RequestHandlerSelectors.basePackage(basePackages) : RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Collections.singletonList(securityScheme()))
                .securityContexts(Collections.singletonList(securityContext()))
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerDocProperties.getTitle())
                .description(swaggerDocProperties.getDescription())
                .license("MIT License")
                .version(swaggerDocProperties.getVersion())
                .licenseUrl(swaggerDocProperties.getUrl())
                .build();
    }

    private springfox.documentation.service.SecurityScheme securityScheme() {
        return new ApiKey("Authorization", "Authorization", "Header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth())
                .forPaths(PathSelectors.any()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Authorization", authorizationScopes));
    }
}
