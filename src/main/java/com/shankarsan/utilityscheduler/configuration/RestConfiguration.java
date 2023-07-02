package com.shankarsan.utilityscheduler.configuration;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.converter.DropboxContentConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestConfiguration {

    private final ApplicationConfiguration applicationConfiguration;

    private final DropboxContentConverter dropboxContentConverter;

    @Bean
    public RestTemplate irctcRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(applicationConfiguration.getUrl(CommonConstants.IRCTC))
                .build();
    }

    @Bean
    public RestTemplate dropboxShortLivedTokenRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(applicationConfiguration.getUrl(CommonConstants.DROPBOX_API))
                .basicAuthentication(applicationConfiguration.getSecret(CommonConstants.DROPBOX_CLIENT_ID),
                        applicationConfiguration.getSecret(CommonConstants.DROPBOX_CLIENT_SECRET))
                .build();
    }

    @Bean
    public RestTemplate dropboxDownloadRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(applicationConfiguration.getUrl(CommonConstants.DROPBOX_CONTENT))
                .messageConverters(dropboxContentConverter)
                .build();
    }
}
