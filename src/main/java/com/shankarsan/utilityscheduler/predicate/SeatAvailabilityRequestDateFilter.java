package com.shankarsan.utilityscheduler.predicate;

import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.parser.SeatAvailabilityDateParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class SeatAvailabilityRequestDateFilter implements Predicate<SeatAvailabilityRequestDto> {

    private final SeatAvailabilityDateParser seatAvailabilityDateParser;

    @Override
    public boolean test(SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
        final Date toDate = Optional.ofNullable(seatAvailabilityRequestDto)
                .map(SeatAvailabilityRequestDto::getToDate)
                .map(seatAvailabilityDateParser::parse)
                .orElseThrow(() -> new IllegalStateException("Unable to get to_date in request date"));
        return Optional.of(seatAvailabilityRequestDto)
                .map(SeatAvailabilityRequestDto::getFromDate)
                .map(seatAvailabilityDateParser::parse)
                .map(date -> !date.before(new Date()) && !date.after(toDate))
                .orElseThrow(() -> new IllegalStateException("Unable to process filter for request date"));
    }
}
