package com.accionmfb.omnix.core.localsource.properties;

import com.accionmfb.omnix.core.commons.StringValues;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "omnix.source.cache")
public class LocalSourceCacheProperties {

    private String sourceTableName = "omnix_generic_param";
    private String paramKeyColumnName = "param_key";
    private String paramValueColumnName = "param_value";
    private String host;
    private String port;
    private String username;
    private String password;
    private String database = "test";
    private String databaseId;
    private boolean fetchOnStartup = true;
    private String defaultParamValue = StringValues.EMPTY_STRING;
    private String dbServerName;
    private String connectorClass = "io.debezium.connector.mysql.MySqlConnector";
    private String connectionName = "my-connector-name";

    private boolean enableVerboseLogging = true;
    private String logFormat = "logging";
}
