package com.shankarsan.utilityscheduler.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@RequiredArgsConstructor
public class RetryConfiguration {

    private final ApplicationConfiguration applicationConfiguration;

    @Bean
    @RefreshScope
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new MaxAttemptsRetryPolicy(applicationConfiguration.getRetryMaxAttempts()));
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setMaxInterval(applicationConfiguration.getRetryMaxInterval());
        backOffPolicy.setMultiplier(applicationConfiguration.getRetryBackoffIntervalMultiplier());
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }
}
