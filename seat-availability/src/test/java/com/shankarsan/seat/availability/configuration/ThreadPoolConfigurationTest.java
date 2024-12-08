package com.shankarsan.seat.availability.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
    ThreadPoolTaskExecutor threadPoolExecutor = threadPoolConfiguration.getThreadPoolTaskExecutor();
    assertEquals(10, threadPoolExecutor.getCorePoolSize());
    assertEquals(100, threadPoolExecutor.getMaxPoolSize());
    assertEquals(1, threadPoolExecutor.getKeepAliveSeconds());
  }
}
