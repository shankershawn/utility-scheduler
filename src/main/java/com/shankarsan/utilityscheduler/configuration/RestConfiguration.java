package com.shankarsan.utilityscheduler.configuration;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestConfiguration {

    private final ApplicationConfiguration applicationConfiguration;

    @Bean
    public RestTemplate irctcRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(applicationConfiguration.getUrl(CommonConstants.IRCTC))
                .build();
    }
}
