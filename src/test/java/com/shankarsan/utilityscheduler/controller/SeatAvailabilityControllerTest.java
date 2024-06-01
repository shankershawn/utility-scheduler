package com.shankarsan.utilityscheduler.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@ExtendWith(MockitoExtension.class)
class SeatAvailabilityControllerTest {

    @Mock
    private Runnable processSeatAvailabilityRunnable;

    @Mock
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @InjectMocks
    private SeatAvailabilityController seatAvailabilityController;

    @Test
    void shouldProcessSeatAvailability() {
        ResponseEntity responseEntity = seatAvailabilityController.processSeatAvailability();
        //doNothing().when(threadPoolExecutor).execute(any());
        //TODO use awaitility here
//        await()
//                .atMost(10, TimeUnit.SECONDS)
//                .untilAsserted(() -> {
//                    verify(seatAvailabilityService, times(1)).processSeatAvailability();
//                });
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }
}
