package com.shankarsan.utilityscheduler.runnable;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.service.SeatAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile(CommonConstants.ON_DEMAND)
public class SeatAvailabilityRunnable {

    private final SeatAvailabilityService seatAvailabilityService;

    @Bean
    public Runnable processSeatAvailabilityRunnable() {
        return () -> {
            log.debug("Processing seat availability");
            seatAvailabilityService.processSeatAvailability();
        };
    }
}
