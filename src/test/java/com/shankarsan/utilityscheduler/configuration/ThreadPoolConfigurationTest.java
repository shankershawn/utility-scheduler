package com.shankarsan.utilityscheduler.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThreadPoolConfigurationTest {

    @Mock
    private ApplicationConfiguration applicationConfiguration;

    @InjectMocks
    private ThreadPoolConfiguration threadPoolConfiguration;

    @Test
    void shouldGetThreadPoolExecutor() {
        when(applicationConfiguration.getCorePoolSize()).thenReturn(10);
        when(applicationConfiguration.getMaximumPoolSize()).thenReturn(100);
        when(applicationConfiguration.getKeepAliveTimeMillis()).thenReturn(1000L);
        ThreadPoolExecutor threadPoolExecutor = threadPoolConfiguration.getThreadPoolExecutor();
        assertEquals(10, threadPoolExecutor.getCorePoolSize());
        assertEquals(100, threadPoolExecutor.getMaximumPoolSize());
        assertEquals(1000L, threadPoolExecutor.getKeepAliveTime(TimeUnit.MILLISECONDS));
    }
}
