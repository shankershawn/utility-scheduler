package com.shankarsan.utilityscheduler.transformers;

import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.parser.SeatAvailabilityDateParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatAvailabilityRequestDateTransformer implements Function<SeatAvailabilityRequestDto, List<Date>> {

    private final SeatAvailabilityDateParser seatAvailabilityDateParser;

    @Override
    public List<Date> apply(SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
        return Optional.ofNullable(seatAvailabilityRequestDto)
                .map(SeatAvailabilityRequestDto::getFromDate)
                .map(seatAvailabilityDateParser::parse).map(date -> {
                    List<Date> callDates = new ArrayList<>();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    while (!calendar.getTime().after(seatAvailabilityDateParser
                            .parse(Optional.ofNullable(seatAvailabilityRequestDto.getToDate())
                                    .orElseThrow(() -> new IllegalArgumentException("Invalid To Date"))))) {
                        callDates.add(calendar.getTime());
                        calendar.add(Calendar.DATE, 6);
                    }
                    return callDates;
                }).orElseThrow(() -> new IllegalStateException("Exception while transforming dates"));
    }
}
