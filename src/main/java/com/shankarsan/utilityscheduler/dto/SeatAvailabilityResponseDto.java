package com.shankarsan.utilityscheduler.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SeatAvailabilityResponseDto implements Serializable {
    private String trainName;
    private List<AvailabilityDayDto> avlDayList;
    private List<EmailDto> emailDtoList;
    private String errorMessage;
    private String mailSubject;
}
