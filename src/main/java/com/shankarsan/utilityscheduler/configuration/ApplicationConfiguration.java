package com.shankarsan.utilityscheduler.configuration;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration("utility-scheduler-appconfig")
@ConfigurationProperties(prefix = "app")
@RequiredArgsConstructor
@Data
@RefreshScope
public class ApplicationConfiguration {

    private Map<String, String> seatAvailabilityConfigurationMap;

    private Map<String, String> secretsMap;

    private Map<String, Integer> availabilityStatusRank;

    private Map<String, String> urlMap;

    private Map<String, String> headerMap;

    private Boolean mailFlag;

    private long apiCallIntervalMillis;

    private int corePoolSize;

    private int maximumPoolSize;

    private long keepAliveTimeMillis;

    private List<String> allowedErrorCodes;

    private int retryMaxAttempts;

    private long retryMaxInterval;

    private double retryBackoffIntervalMultiplier;


    public String getCronExpression(Map<String, String> configMap) {
        return extractFromMap(configMap, CommonConstants.CRON_EXPRESSION);
    }

    public String getUrl(String key) {
        return extractFromMap(this.urlMap, key);
    }

    private String extractFromMap(Map<String, String> mapToExtract, String key) {
        return Optional.ofNullable(mapToExtract).map(e -> e.get(key)).orElse(null);
    }
}
