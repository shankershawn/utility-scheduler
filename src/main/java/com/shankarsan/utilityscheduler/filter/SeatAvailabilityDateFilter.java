package com.shankarsan.utilityscheduler.filter;

import com.shankarsan.utilityscheduler.dto.AvailabilityDayDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.parser.SeatAvailabilityDateParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatAvailabilityDateFilter {

    private final SeatAvailabilityDateParser seatAvailabilityDateParser;

    public Predicate<AvailabilityDayDto> filterSeatAvailabilityData
            (SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        return availabilityDayDto -> {
            Date requestToDate = Optional.ofNullable(seatAvailabilityResponseDto)
                    .map(SeatAvailabilityResponseDto::getSeatAvailabilityRequestDto)
                    .map(SeatAvailabilityRequestDto::getToDate)
                    .map(seatAvailabilityDateParser::parse)
                    .orElseThrow(() -> new IllegalStateException("To date is not available"));
            return Optional.ofNullable(availabilityDayDto)
                    .map(AvailabilityDayDto::getAvailabilityDate)
                    .map(seatAvailabilityDateParser::parse)
                    .map(date -> !date.after(requestToDate))
                    .orElseThrow(() -> new IllegalStateException("Something went wrong during date filtering"));
        };
    }
}
