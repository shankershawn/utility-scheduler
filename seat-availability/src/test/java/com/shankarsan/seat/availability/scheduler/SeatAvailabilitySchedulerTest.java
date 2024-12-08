package com.shankarsan.seat.availability.scheduler;

import com.shankarsan.seat.availability.configuration.ApplicationConfiguration;
import com.shankarsan.seat.availability.service.SeatAvailabilityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SeatAvailabilitySchedulerTest {

  @Mock
  private ApplicationConfiguration applicationConfiguration;

  @Mock
  private SeatAvailabilityService seatAvailabilityService;

  @InjectMocks
  private SeatAvailabilityScheduler seatAvailabilityScheduler;

  @Test
  void shouldReturnCronTrigger() {
    when(applicationConfiguration.getSeatAvailabilityConfigurationMap()).thenReturn(new HashMap<>());
    when(applicationConfiguration.getCronExpression(any(HashMap.class))).thenReturn("* * * * * *");
    assertNotNull(seatAvailabilityScheduler.cronTrigger());
  }

  @Test
  void shouldConfigureTasks() {
    when(applicationConfiguration.getSeatAvailabilityConfigurationMap()).thenReturn(new HashMap<>());
    when(applicationConfiguration.getCronExpression(any(HashMap.class))).thenReturn("* * * * * *");
    ScheduledTaskRegistrar scheduledTaskRegistrar = new ScheduledTaskRegistrar();
    seatAvailabilityScheduler.configureTasks(scheduledTaskRegistrar);
  }


}
