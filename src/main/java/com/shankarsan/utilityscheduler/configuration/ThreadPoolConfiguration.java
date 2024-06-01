package com.shankarsan.utilityscheduler.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ThreadPoolConfiguration {

    private final ApplicationConfiguration applicationConfiguration;

    @Bean
    @RefreshScope
    public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        log.debug("Creating threadPoolTaskExecutor bean");
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(applicationConfiguration.getCorePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(applicationConfiguration.getMaximumPoolSize());
        threadPoolTaskExecutor.setKeepAliveSeconds((int) applicationConfiguration.getKeepAliveTimeMillis() / 1000);
        return threadPoolTaskExecutor;
    }
}
