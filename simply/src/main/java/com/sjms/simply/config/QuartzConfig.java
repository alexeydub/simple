package com.sjms.simply.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ConfigurationProperties(prefix = "spring.quartz-datasource")
@EnableTransactionManagement
public class QuartzConfig {
    
    private String url;
    
    @Bean(name = "quartzDataSource")
    @QuartzDataSource
    public DataSource quartzDataSource() {
        return DataSourceBuilder.create().url(url).build();    
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
