package com.shankarsan.seat.availability.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatAvailabilityResponseDto implements Serializable {

    private static final long serialVersionUID = -2L;

    private String trainName;
    private List<AvailabilityDayDto> avlDayList;
    private List<EmailDto> emailDtoList;
    private String errorMessage;
    private String mailSubject;
    private transient SeatAvailabilityRequestDto seatAvailabilityRequestDto;
}
