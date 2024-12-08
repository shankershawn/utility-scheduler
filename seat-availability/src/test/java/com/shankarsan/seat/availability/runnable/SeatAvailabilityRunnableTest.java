package com.shankarsan.seat.availability.runnable;

import com.shankarsan.seat.availability.service.SeatAvailabilityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SeatAvailabilityRunnableTest {

  @InjectMocks
  private SeatAvailabilityRunnable seatAvailabilityRunnable;

  @Mock
  private SeatAvailabilityService seatAvailabilityService;

  @Test
  void shouldStartRunnable() {
    Runnable runnable = seatAvailabilityRunnable.getSeatAvailabilityRunnable();
    runnable.run();
    verify(seatAvailabilityService, times(1)).processSeatAvailability();
  }
}
