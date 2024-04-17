package com.accionmfb.omnix.core.jwt.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "omnix.security.default")
public class DefaultJwtProperties {
    private String jwtKey = "j3H5Ld5nYmGWyULy6xwpOgfSH++NgKXnJMq20vpfd+8=t";
    private String idTokenKey = "idToken";
    private String appUserTokenInHr = "24";
    private String idTokenExpiryInMin = "30";
    private String adminUserTokenExpirationInMin = "30";
}
