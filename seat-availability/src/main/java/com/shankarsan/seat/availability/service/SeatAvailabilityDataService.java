package com.shankarsan.seat.availability.service;

import com.shankarsan.seat.availability.dto.SeatAvailabilityRequestDto;
import com.shankarsan.seat.availability.dto.SeatAvailabilityResponseDto;

public interface SeatAvailabilityDataService {

    SeatAvailabilityResponseDto fetchAvailabilityData(SeatAvailabilityRequestDto seatAvailabilityRequestDto);
}
