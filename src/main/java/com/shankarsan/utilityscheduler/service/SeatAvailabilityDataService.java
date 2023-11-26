package com.shankarsan.utilityscheduler.service;

import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;

public interface SeatAvailabilityDataService {

    SeatAvailabilityResponseDto fetchAvailabilityData(SeatAvailabilityRequestDto seatAvailabilityRequestDto);
}
