package com.shankarsan.seat.availability.scheduler;

import com.shankarsan.seat.availability.configuration.ApplicationConfiguration;
import com.shankarsan.seat.availability.constants.CommonConstants;
import com.shankarsan.seat.availability.service.SeatAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

@EnableScheduling
@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile(CommonConstants.SCHEDULER)
public class SeatAvailabilityScheduler implements SchedulingConfigurer {

  private final ApplicationConfiguration applicationConfiguration;

  private final SeatAvailabilityService seatAvailabilityService;

  @Bean
  @RefreshScope
  public CronTrigger cronTrigger() {
    return new CronTrigger(applicationConfiguration
        .getCronExpression(applicationConfiguration
            .getSeatAvailabilityConfigurationMap()));
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    log.info("Configuring scheduled task");
    taskRegistrar.addCronTask(new CronTask(seatAvailabilityService::processSeatAvailability,
        cronTrigger()));
  }
}
