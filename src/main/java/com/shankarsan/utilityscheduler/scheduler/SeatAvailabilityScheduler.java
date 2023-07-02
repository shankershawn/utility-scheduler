package com.shankarsan.utilityscheduler.scheduler;

import com.shankarsan.utilityscheduler.configuration.ApplicationConfiguration;
import com.shankarsan.utilityscheduler.service.SeatAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

@EnableScheduling
@Configuration
@RequiredArgsConstructor
@Slf4j
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
        taskRegistrar.addCronTask(new CronTask(seatAvailabilityService::processSeatAvailability,
                cronTrigger()));
    }
}
