package com.shankarsan.utilityscheduler.configuration;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration("utility-scheduler-restconfig")
@RequiredArgsConstructor
public class RestConfiguration {

    private final ApplicationConfiguration applicationConfiguration;

    @Bean(CommonConstants.IRCTC + "Template")
    public RestTemplate irctcRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(applicationConfiguration.getUrl(CommonConstants.IRCTC))
                .build();
    }

    @Bean(CommonConstants.CONFIRM_TKT + "Template")
    public RestTemplate confirmTktRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(applicationConfiguration.getUrl(CommonConstants.CONFIRM_TKT))
                .build();
    }
}
