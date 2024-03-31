package com.accionmfb.omnix.core.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ToString
@ConfigurationProperties(prefix = "spring.datasource")
public class DatasourceConfigurationProperties {
    private String driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private String url = "jdbc:sqlserver://localhost;databaseName=omnix;encrypt=true;trustServerCertificate=true;";
    private String username = "omnixservice";
    private String password = "Ab123456";
    private String host = "localhost";
    private String port = "3306";
}
