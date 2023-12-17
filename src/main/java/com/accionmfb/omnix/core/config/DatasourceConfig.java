package com.accionmfb.omnix.core.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


@Data
@Slf4j
@Configuration
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = DatasourceConfigurationProperties.class)
public class DatasourceConfig {

    private final DatasourceConfigurationProperties datasourceConfigurationProperties;

    @Bean
    @ConditionalOnMissingBean(value = DataSource.class)
    public DataSource dataSource(){
        try {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(datasourceConfigurationProperties.getDriverClassName());
            dataSource.setUrl(datasourceConfigurationProperties.getUrl());
            dataSource.setUsername(datasourceConfigurationProperties.getUsername());
            dataSource.setPassword(datasourceConfigurationProperties.getPassword());
            return dataSource;
        }catch (Exception exception){
            log.info("Error occurred while connecting to datasource for configurations. Exception message is: {}", exception.getMessage());
            return null;
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        try {
            return new JdbcTemplate(dataSource);
        }catch (Exception exception){
            log.error("Exception occurred while creating JdbcTemplate for datasource configuration. Exception message is: {}", exception.getMessage());
            return null;
        }
    }
}
