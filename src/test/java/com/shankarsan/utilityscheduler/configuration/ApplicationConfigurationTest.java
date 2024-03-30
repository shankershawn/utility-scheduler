package com.shankarsan.utilityscheduler.configuration;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigurationTest {

    @InjectMocks
    private static ApplicationConfiguration applicationConfiguration;

    @Test
    void shouldReturnConfigValues() {
        assertEquals(Map.of("headerKey", "headerValue"), applicationConfiguration.getHeaderMap());
        assertEquals(Map.of("configKey", "configValue"), applicationConfiguration
                .getSeatAvailabilityConfigurationMap());
        assertEquals(Boolean.TRUE, applicationConfiguration.getMailFlag());
        assertEquals(1000L, applicationConfiguration.getApiCallIntervalMillis());
        assertEquals(Map.of("secretKey", "secretValue"), applicationConfiguration.getSecretsMap());
        assertEquals(10, applicationConfiguration.getCorePoolSize());
        assertEquals(Map.of("urlKey", "urlValue"), applicationConfiguration.getUrlMap());
        assertEquals(100, applicationConfiguration.getMaximumPoolSize());
        assertEquals(1000L, applicationConfiguration.getKeepAliveTimeMillis());
    }

    @Test
    void shouldGetCronExpressionForPopulatedMap() {
        assertEquals("testCron", applicationConfiguration
                .getCronExpression(Map.of(CommonConstants.CRON_EXPRESSION, "testCron"))
        );
    }

    @Test
    void shouldGetCronExpressionForIncorrectKey() {
        assertNull(applicationConfiguration
                .getCronExpression(Map.of("incorrectKey", "testCron"))
        );
    }

    @Test
    void shouldGetCronExpressionForNullMap() {
        assertNull(applicationConfiguration
                .getCronExpression(null)
        );
    }

    @Test
    void shouldGetAllowedErrorCodes() {
        assertNotNull(applicationConfiguration
                .getAllowedErrorCodes()
        );
    }

    @Test
    void shouldgetUrl() {
        assertEquals("urlValue", applicationConfiguration.getUrl("urlKey"));
    }

    @BeforeAll
    static void populateApplicationConfiguration() {
        applicationConfiguration = new ApplicationConfiguration();
        applicationConfiguration.setHeaderMap(Map.of("headerKey", "headerValue"));
        applicationConfiguration.setSeatAvailabilityConfigurationMap(Map.of("configKey", "configValue"));
        applicationConfiguration.setMailFlag(Boolean.TRUE);
        applicationConfiguration.setApiCallIntervalMillis(1000L);
        applicationConfiguration.setSecretsMap(Map.of("secretKey", "secretValue"));
        applicationConfiguration.setCorePoolSize(10);
        applicationConfiguration.setUrlMap(Map.of("urlKey", "urlValue"));
        applicationConfiguration.setMaximumPoolSize(100);
        applicationConfiguration.setKeepAliveTimeMillis(1000L);
        applicationConfiguration.setAllowedErrorCodes(List.of("errorCode1", "errorCode2"));
    }

}
