package com.accionmfb.omnix.core.localsource.config;

import com.accionmfb.omnix.core.annotation.RequiredOmnixParam;
import com.accionmfb.omnix.core.commons.ConfigSourceOperation;
import com.accionmfb.omnix.core.commons.SMART_PRINT_FORMAT;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.config.DatasourceConfigurationProperties;
import com.accionmfb.omnix.core.event.data.ConfigSourcePropertyChangedEvent;
import com.accionmfb.omnix.core.localsource.properties.LocalSourceProperties;
import com.accionmfb.omnix.core.registry.LocalSourceCacheRegistry;
import com.accionmfb.omnix.core.service.DatasourceService;
import com.accionmfb.omnix.core.util.CommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.accionmfb.omnix.core.localsource.config.SimpleLocalConfigurer.DebeziumPropertyKey.*;
import static com.accionmfb.omnix.core.util.OmnixCoreApplicationUtil.returnOrdefault;

@Slf4j
@Configuration

@RequiredArgsConstructor
@EnableConfigurationProperties(value = { LocalSourceProperties.class, DatasourceConfigurationProperties.class })
public class SimpleLocalConfigurer {

    private final Reflections reflections;
    private final ObjectMapper objectMapper;
    private final DatasourceService datasourceService;
    private final ApplicationEventPublisher eventPublisher;
    private final LocalSourceProperties localSourceProperties;
    private final DatasourceConfigurationProperties datasourceProperties;


    @Bean
    public String initParamSourceConfigurationFromDatabase(){
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RequiredOmnixParam.class);
        if(localSourceProperties.isFetchOnStartup()) {
            for (Class<?> clazz : classes) {
                if (clazz.isEnum()) {
                    List<String> params = getEnumeratedParams(clazz);
                    CommonUtil.runIf(!params.isEmpty(), ()->{
                        Map<String, String> paramsMap = datasourceService.getOmnixParams(params);
                        LocalSourceCacheRegistry.setAllSource(paramsMap);
                        LocalSourceCacheRegistry.setConfigKeys(params);
                    });
                }
            }
            log.info("Omnix Params registered to local cache successfully");
            LocalSourceCacheRegistry.setConfigKeys(LocalSourceCacheRegistry.getKeys());
            if(localSourceProperties.isEnableVerboseLogging()){
                LocalSourceCacheRegistry.printRegistry(getCacheLogFormat(localSourceProperties.getLogFormat()), localSourceProperties);
            }
        }
        return "Success";
    }


    @EventListener(value = ApplicationStartedEvent.class)
    public void initDebeziumDataSourceChangeDataCapture(){
        if(localSourceProperties.isFetchOnStartup()) {
            try {
                Properties props = getConnectorProperties();
                DebeziumEngine<ChangeEvent<String, String>> engine = DebeziumEngine.create(Json.class)
                        .using(props)
                        .notifying((list, recordCommitter) -> list.forEach(stringStringChangeEvent -> {
                            String schemeJson = stringStringChangeEvent.value();
                            try {
                                DebeziumScheme scheme = objectMapper.readValue(schemeJson, DebeziumScheme.class);
                                DebeziumPayload payload = scheme.getPayload();
                                if (Objects.nonNull(payload) && Objects.nonNull(payload.getAfter())) {
                                    Map<String, Object> afterChange = payload.getAfter();
                                    log.info("Change Captured: {}", afterChange);
                                    String paramKey = String.valueOf(afterChange.get(localSourceProperties.getParamKeyColumnName()));
                                    String paramValue = String.valueOf(afterChange.get(localSourceProperties.getParamValueColumnName()));
                                    ConfigSourcePropertyChangedEvent event = ConfigSourcePropertyChangedEvent.of(afterChange, paramKey, paramValue, ConfigSourceOperation.UPDATE);
                                    eventPublisher.publishEvent(event);
                                }
                            } catch (JsonProcessingException e) {
                                log.error("Exception encountered while publishing CDC event. Exception message is: {}", e.getMessage());
                            }
                        }))
                        .build();
                engine.run();
            } catch (Exception e) {
                log.error("Exception encountered during CDC initialization. Exception message is: {}", e.getMessage());
            }
        }
    }


    private List<String> getEnumeratedParams(Class<?> enumClass){
        return Arrays.stream(enumClass.getDeclaredFields())
                .filter(Objects::nonNull)
                .map(Field::getName)
                .filter(name -> !name.equalsIgnoreCase("$VALUES"))
                .collect(Collectors.toList());
    }

    private SMART_PRINT_FORMAT getCacheLogFormat(String formatStr){
        try {
            return SMART_PRINT_FORMAT.valueOf(formatStr.toUpperCase());
        }catch (Exception exception){
            return SMART_PRINT_FORMAT.LOGGING;
        }
    }

    private Properties getConnectorProperties(){
        Properties props = new Properties();
        props.setProperty(NAME, localSourceProperties.getConnectionName());
        props.setProperty(CONNECTOR_CLASS, localSourceProperties.getConnectorClass());
        props.setProperty(DATABASE_HOSTNAME, returnOrdefault(localSourceProperties.getHost(), datasourceProperties.getHost()));
        props.setProperty(DATABASE_PORT, returnOrdefault(localSourceProperties.getPort(), datasourceProperties.getPort()));
        props.setProperty(DATABASE_USER, returnOrdefault(localSourceProperties.getUsername(), datasourceProperties.getUsername()));
        props.setProperty(DATABASE_PASSWORD, returnOrdefault(localSourceProperties.getPassword(), datasourceProperties.getPassword()));
        props.setProperty(DB_NAME, localSourceProperties.getDatabase());
        props.setProperty(DB_SERVER_ID, returnOrdefault(localSourceProperties.getDatabaseId(), String.valueOf(System.currentTimeMillis())));
        props.setProperty(DB_SERVER_NAME, returnOrdefault(localSourceProperties.getDbServerName(), UUID.randomUUID().toString()));
        props.setProperty(TABLE_INCLUDE_LIST, String.join(StringValues.DOT, localSourceProperties.getDatabase(), localSourceProperties.getSourceTableName()));
        props.setProperty(OFFSET_STORAGE, "org.apache.kafka.connect.storage.MemoryOffsetBackingStore");
        props.setProperty(SCHEMA_HISTORY_INTERNAL, "io.debezium.relational.history.MemorySchemaHistory");
        props.setProperty(TOPIC_PREFIX, "embedded");
        props.setProperty(OFFSET_FLUSH_INTERVAL, "1000");
        props.setProperty(SNAPSHOT_MODE, "schema_only");
        props.setProperty(DB_CONNECTION_TIME_ZONE, StringValues.EMPTY_STRING);
        props.setProperty(WHITELIST_DATABASE_CAPTURE, "true");
        props.setProperty(WHITELIST_TABLE_CAPTURE, "true");
        props.setProperty(LOG_LEVEL, "ERROR");
        props.setProperty("database.names", localSourceProperties.getDbServerName());
        return props;
    }

    public interface DebeziumPropertyKey{
        String NAME = "name";
        String CONNECTOR_CLASS = "connector.class";
        String DATABASE_HOSTNAME = "database.hostname";
        String DATABASE_PORT = "database.port";
        String DATABASE_USER = "database.user";
        String DATABASE_PASSWORD = "database.password";
        String DB_NAME = "database.dbname";
        String DB_SERVER_ID = "database.server.id";
        String DB_SERVER_NAME = "database.server.name";
        String TABLE_INCLUDE_LIST = "table.include.list";
        String OFFSET_STORAGE = "offset.storage";
        String SCHEMA_HISTORY_INTERNAL = "schema.history.internal";
        String TOPIC_PREFIX = "topic.prefix";
        String OFFSET_FLUSH_INTERVAL = "offset.flush.interval.ms";
        String SNAPSHOT_MODE = "snapshot.mode";
        String DB_CONNECTION_TIME_ZONE = "database.connectionTimeZone";
        String LOG_LEVEL = "log4j2.level.io.debezium";
        String WHITELIST_DATABASE_CAPTURE = "schema.history.internal.store.only.captured.databases.ddl";
        String WHITELIST_TABLE_CAPTURE = "schema.history.internal.store.only.captured.tables.ddl";
    }
}
