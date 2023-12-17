package com.shankarsan.utilityscheduler.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ThreadPoolConfiguration {

    private final ApplicationConfiguration applicationConfiguration;

    @Bean
    @RefreshScope
    public ThreadPoolExecutor getThreadPoolExecutor() {
        log.debug("Creating getThreadPoolExecutor bean");
        return new ThreadPoolExecutor(applicationConfiguration.getCorePoolSize(),
                applicationConfiguration.getMaximumPoolSize(), applicationConfiguration.getKeepAliveTimeMillis(),
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }
}
