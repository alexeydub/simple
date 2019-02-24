package com.sjms.simply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.sjms.simply.config.Config;

@SpringBootApplication
@EnableConfigurationProperties(Config.class)
public class SimpleJobManagementApplication {

    private static final Logger log = LoggerFactory
            .getLogger(SimpleJobManagementApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SimpleJobManagementApplication.class, args);
    }

    @Autowired
    private Config config;
    
    @Bean(name = "executor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getCorePoolSize());
        executor.setMaxPoolSize(config.getMaxPoolSize());
        executor.setQueueCapacity(config.getQueueCapacity());
        return executor;
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            log.info("Simple Job Management System started");
        };
    }
}
