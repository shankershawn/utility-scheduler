package com.shankarsan.utilityscheduler.dto;

import lombok.Data;

import java.util.List;

@Data
public class SeatAvailabilityResponseDto {
    private List<AvailabilityDayDto> avlDayList;
    private String errorMessage;
}
