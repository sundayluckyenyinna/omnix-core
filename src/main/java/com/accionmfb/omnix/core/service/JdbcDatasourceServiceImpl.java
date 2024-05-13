package com.accionmfb.omnix.core.service;

import com.accionmfb.omnix.core.commons.ConfigSourceOperation;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.event.data.ConfigSourcePropertyChangedEvent;
import com.accionmfb.omnix.core.localsource.properties.LocalSourceProperties;
import com.accionmfb.omnix.core.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Configuration
@RequiredArgsConstructor
@ConditionalOnBean(value = JdbcTemplate.class)
public class JdbcDatasourceServiceImpl implements DatasourceService{

    private final JdbcTemplate jdbcTemplate;
    private final ApplicationEventPublisher publisher;
    private final LocalSourceProperties localSourceProperties;

    @Override
    public Map<String, String> getAllOmnixParams(){
        String tableName = localSourceProperties.getSourceTableName();
        String paramKeyColumnName = localSourceProperties.getParamKeyColumnName();
        String paramValueColumnName = localSourceProperties.getParamValueColumnName();
        String defaultValue = localSourceProperties.getDefaultParamValue();

        String sql = String.format("select %s, %s from %s", paramKeyColumnName, paramValueColumnName, tableName);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        return getStringStringMap(defaultValue, maps);
    }

    @Override
    public Map<String, String> getOmnixParams(List<String> requiredParamKeys){
        String tableName = localSourceProperties.getSourceTableName();
        String paramKeyColumnName = localSourceProperties.getParamKeyColumnName();
        String paramValueColumnName = localSourceProperties.getParamValueColumnName();
        String defaultValue = localSourceProperties.getDefaultParamValue();

        String inSql = String.join(",", Collections.nCopies(requiredParamKeys.size(), "?"));
        String sql = String.format("select %s, %s from %s where %s in (%s)", paramKeyColumnName, paramValueColumnName, tableName, paramKeyColumnName, inSql);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, requiredParamKeys.toArray());
        return getStringStringMap(defaultValue, maps);
    }

    private Map<String, String> getStringStringMap(String defaultValue, List<Map<String, Object>> maps) {
        List<Map<String, String>> stringMap  = maps.stream().map(map -> {
            Map<String, String> res = new HashMap<>();
            for(Map.Entry<String, Object> entry : map.entrySet()){
                if(Objects.nonNull(entry.getKey())) {
                    if(Objects.nonNull(entry.getValue())) {
                        res.put(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                    else if(Objects.nonNull(defaultValue)){
                        res.put(entry.getKey(), defaultValue);
                    }
                }
            }
            return res;
        }).collect(Collectors.toList());

        Map<String, String> paramAndValue = new HashMap<>();
        stringMap.forEach(map -> {
            String key = map.get(localSourceProperties.getParamKeyColumnName());
            String value = map.get(localSourceProperties.getParamValueColumnName());
            paramAndValue.put(key, value);
        });

        return paramAndValue;
    }

    @Override
    public <T> boolean saveOmnixParams(T paramKey, String paramValue){
        String tableName = localSourceProperties.getSourceTableName();
        String paramKeyColumnName = localSourceProperties.getParamKeyColumnName();
        String paramValueColumnName = localSourceProperties.getParamValueColumnName();

        String sql = String.format("insert into %s(%s, %s) values (?, ?)", tableName, paramKeyColumnName, paramValueColumnName);
        try {
            String now = CommonUtil.getCurrentDateTime().toString();
            int affectedRows = jdbcTemplate.update(sql, String.valueOf(paramKey), paramValue);
            if (affectedRows > 0) {
                log.info("New Omnix generic param with name : {} saved successfully", paramKey);
                publisher.publishEvent(ConfigSourcePropertyChangedEvent.of(this, String.valueOf(paramKey), paramValue, ConfigSourceOperation.SAVE));
                return true;
            }
            return false;
        }catch(Exception ex){
            log.warn("Could not execute save operation for save new omnix generic param with key: {}", paramKey);
            return false;
        }
    }

    @Override
    public <T> boolean updateOmnixParam(T paramKey, String newParamValue){
        String tableName = localSourceProperties.getSourceTableName();
        String paramKeyColumnName = localSourceProperties.getParamKeyColumnName();
        String paramValueColumnName = localSourceProperties.getParamValueColumnName();

        String sql = String.format("update %s set %s = ? where %s = ?", tableName, paramValueColumnName, paramKeyColumnName);
        int affectedRows = jdbcTemplate.update(sql, newParamValue, String.valueOf(paramKey));
        if(affectedRows > 0){
            log.info("Omnix generic param with name : {} updated successfully", paramKey);
            publisher.publishEvent(ConfigSourcePropertyChangedEvent.of(this, String.valueOf(paramKey), newParamValue, ConfigSourceOperation.UPDATE));
            return true;
        }else{
            log.warn("Could not execute update operation for update omnix generic param with key: {}", paramKey);
            return false;
        }
    }

    @Override
    public boolean deleteOmnixParam(String key){
        String tableName = localSourceProperties.getSourceTableName();
        String paramKeyColumnName = localSourceProperties.getParamKeyColumnName();

        String sql = String.format("delete from %s where %s = ?", tableName, paramKeyColumnName);
        int affectedRows = jdbcTemplate.update(sql, key);
        if(affectedRows > 0){
            log.info("Omnix generic param with name: {} deleted successfully", key);
            publisher.publishEvent(ConfigSourcePropertyChangedEvent.of(this, String.valueOf(key), null, ConfigSourceOperation.DELETE));
            return true;
        }else{
            log.info("Could not perform DELETE operation for omnix param with key: {}", key);
            return false;
        }
    }

    @Override
    public List<String> getLocalCacheParamKeys(){
        String sql = String.format("select %s from %s", localSourceProperties.getParamKeyColumnName(), localSourceProperties.getSourceTableName());
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public List<String> getLocalCacheParamValues(){
        String sql = String.format("select %s from %s", localSourceProperties.getParamValueColumnName(), localSourceProperties.getSourceTableName());
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public String getParamValue(Object key){
        String sql = String.format("select %s from %s where %s = ?", localSourceProperties.getParamValueColumnName(), localSourceProperties.getSourceTableName(), localSourceProperties.getParamKeyColumnName());
        List<String> paramValueList = jdbcTemplate.queryForList(sql, String.class, String.valueOf(key));
        return paramValueList.stream().findFirst().orElse(null);
    }

    @Override
    public String getParamValue(String key){
        return getParamValue((Object) key);
    }

    @Override
    public String getParamValueOrDefault(Object paramKey, String defaultValue, boolean createIfExist){
        String paramValue = this.getParamValue(paramKey);
        if(Objects.isNull(paramValue) && createIfExist){
            saveOmnixParams(paramKey, defaultValue);
            return defaultValue;
        }
        return Objects.isNull(paramValue) ? defaultValue : paramValue;
    }

    @Override
    public String getParamValueOrDefault(Object paramKey, String defaultValue){
        return getParamValueOrDefault(paramKey, defaultValue, true);
    }

    @Override
    public boolean saveIdTokenUnawareEndpoint(String endpoint, String msName){
        String sql = "insert into id_token_unaware_endpoint(created_at, updated_at, endpoint, micro_service_name) values('%s', '%s', '%s', '%s')";
        String now = CommonUtil.getCurrentDateTime().toString();
        sql = String.format(sql, now, now, endpoint, msName);
        return jdbcTemplate.update(sql) > 0;
    }

    @Override
    public boolean deleteIdTokenUnawareEndpoint(String endpoint){
        String sql = "delete from id_token_unaware_endpoint where endpoint = '%s'";
        sql = String.format(sql, endpoint);
        return jdbcTemplate.update(sql) > 0;
    }

    @Override
    public boolean hasEndpoint(String endpoint){
        String sql = "select * from id_token_unaware_endpoint where endpoint = '%s'";
        sql = String.format(sql, endpoint);
        return !jdbcTemplate.queryForList(sql).isEmpty();
    }
}
