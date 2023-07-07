package com.shankarsan.utilityscheduler.dto;

import lombok.Data;

import java.util.List;

@Data
public class SeatAvailabilityResponseDto {
    private String trainName;
    private List<AvailabilityDayDto> avlDayList;
    private List<EmailDto> emailDtoList;
    private String errorMessage;
    private String mailSubject;
}
