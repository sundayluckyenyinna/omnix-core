package com.accionmfb.omnix.core.localcache.properties;

import com.accionmfb.omnix.core.commons.StringValues;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "omnix.source.cache")
public class LocalSourceCacheProperties {

    private String sourceTableName = "omnix_generic_params";
    private String paramKeyColumnName = "param_key";
    private String paramValueColumnName = "param_value";
    private boolean fetchOnStartup = true;
    private String defaultParamValue = StringValues.EMPTY_STRING;
    private boolean enableVerboseLogging = true;
    private String logFormat = "logging";
}
