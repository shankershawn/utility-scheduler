package com.shankarsan.utilityscheduler.controller;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.service.SeatAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/v1/seats")
@Slf4j
@RequiredArgsConstructor
@Profile(CommonConstants.ON_DEMAND)
public class SeatAvailabilityController {

    private final SeatAvailabilityService seatAvailabilityService;

    private final ThreadPoolExecutor threadPoolExecutor;

    @PostMapping
    public ResponseEntity<Void> processSeatAvailability() {

        CompletableFuture.runAsync(() -> {
            log.debug("Processing seat availability");
            seatAvailabilityService.processSeatAvailability();
        }, threadPoolExecutor).thenApply(v -> "Done processing seat availability").thenAccept(log::debug);
        log.debug("Submitted request for seat availability process");
        return ResponseEntity.noContent().build();
    }
}
