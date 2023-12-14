package com.shankarsan.utilityscheduler.configuration;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationConfigurationTest {

    @InjectMocks
    private ApplicationConfiguration applicationConfiguration;

    @Test
    void shouldReturnConfigValues() {
        assertEquals(Map.of("headerKey", "headerValue"), this.applicationConfiguration.getHeaderMap());
        assertEquals(Map.of("configKey", "configValue"), this.applicationConfiguration
                .getSeatAvailabilityConfigurationMap());
        assertEquals(Boolean.TRUE, this.applicationConfiguration.getMailFlag());
        assertEquals(1000L, this.applicationConfiguration.getApiCallIntervalMillis());
        assertEquals(Map.of("secretKey", "secretValue"), this.applicationConfiguration.getSecretsMap());
        assertEquals(10, this.applicationConfiguration.getCorePoolSize());
        assertEquals(Map.of("urlKey", "urlValue"), this.applicationConfiguration.getUrlMap());
        assertEquals(100, this.applicationConfiguration.getMaximumPoolSize());
        assertEquals(1000L, this.applicationConfiguration.getKeepAliveTimeMillis());
    }

    @Test
    void shouldGetCronExpressionForPopulatedMap() {
        assertEquals("testCron", this.applicationConfiguration
                .getCronExpression(Map.of(CommonConstants.CRON_EXPRESSION, "testCron"))
        );
    }

    @Test
    void shouldGetCronExpressionForIncorrectKey() {
        assertNull(this.applicationConfiguration
                .getCronExpression(Map.of("incorrectKey", "testCron"))
        );
    }

    @Test
    void shouldGetCronExpressionForNullMap() {
        assertNull(this.applicationConfiguration
                .getCronExpression(null)
        );
    }

    @Test
    void shouldgetUrl() {
        assertEquals("urlValue", this.applicationConfiguration.getUrl("urlKey"));
    }

    @BeforeEach
    public void populateApplicationConfiguration() {
        this.applicationConfiguration.setHeaderMap(Map.of("headerKey", "headerValue"));
        this.applicationConfiguration.setSeatAvailabilityConfigurationMap(Map.of("configKey", "configValue"));
        this.applicationConfiguration.setMailFlag(Boolean.TRUE);
        this.applicationConfiguration.setApiCallIntervalMillis(1000L);
        this.applicationConfiguration.setSecretsMap(Map.of("secretKey", "secretValue"));
        this.applicationConfiguration.setCorePoolSize(10);
        this.applicationConfiguration.setUrlMap(Map.of("urlKey", "urlValue"));
        this.applicationConfiguration.setMaximumPoolSize(100);
        this.applicationConfiguration.setKeepAliveTimeMillis(1000L);
    }

}
