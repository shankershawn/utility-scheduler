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
                .map(seatAvailabilityDateParser::parse)
                .map(date -> {
                    List<Date> callDates = new ArrayList<>();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    while (!calendar.getTime().after(seatAvailabilityDateParser
                            .parse(Optional.ofNullable(seatAvailabilityRequestDto.getToDate())
                                    .orElseThrow(() -> new IllegalArgumentException("Invalid To Date"))))) {
                        // Below section validates if the calculated train run date is a valid train run date.
                        // If not, then date is incremented by 1 day until a valid train run date is found
                        while (!seatAvailabilityRequestDto.getRunDays()
                                .contains(calendar.get(Calendar.DAY_OF_WEEK))) {
                            calendar.add(Calendar.DATE, 1);
                        }
                        callDates.add(calendar.getTime());
                        calendar.add(Calendar.DATE, 6);
                    }
                    return callDates;
                })
                .orElseThrow(() -> new IllegalStateException("Exception while transforming dates"));
    }
}
