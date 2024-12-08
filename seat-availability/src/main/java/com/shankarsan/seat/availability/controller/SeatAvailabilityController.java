package com.shankarsan.seat.availability.controller;

import com.shankarsan.seat.availability.constants.CommonConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1/seats")
@Slf4j
@RequiredArgsConstructor
@Profile(CommonConstants.ON_DEMAND)
public class SeatAvailabilityController {

  private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

  private final Runnable processSeatAvailabilityRunnable;

  @GetMapping
  public ResponseEntity<Void> processSeatAvailability() {
    CompletableFuture
        .runAsync(processSeatAvailabilityRunnable, threadPoolTaskExecutor)
        .thenApply(v -> "Done processing seat availability")
        .thenAccept(log::debug);
    log.debug("Submitted request for seat availability process");
    return ResponseEntity.noContent().build();
  }
}
