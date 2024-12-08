package com.shankarsan.seat.availability.runnable;

import com.shankarsan.seat.availability.constants.CommonConstants;
import com.shankarsan.seat.availability.service.SeatAvailabilityService;
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
  public Runnable getSeatAvailabilityRunnable() {
    return () -> {
      log.debug("Processing seat availability");
      seatAvailabilityService.processSeatAvailability();
    };
  }
}
