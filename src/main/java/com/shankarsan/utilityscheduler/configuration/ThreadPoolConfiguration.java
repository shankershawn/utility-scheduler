package com.shankarsan.utilityscheduler.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfiguration {

    private final ApplicationConfiguration applicationConfiguration;

    @Bean
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return new ThreadPoolExecutor(applicationConfiguration.getCorePoolSize(),
                applicationConfiguration.getMaximumPoolSize(), applicationConfiguration.getKeepAliveTimeMillis(),
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }
}
