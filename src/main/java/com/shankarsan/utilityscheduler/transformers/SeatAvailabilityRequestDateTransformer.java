package com.shankarsan.utilityscheduler.transformers;

import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

@Component
@Slf4j
public class SeatAvailabilityRequestDateTransformer implements Function<SeatAvailabilityRequestDto, List<Date>> {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public List<Date> apply(SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
        return Optional.ofNullable(seatAvailabilityRequestDto)
                .map(SeatAvailabilityRequestDto::getFromDate)
                .map(dateString -> {
                    Date fromDate;
                    try {
                        fromDate = simpleDateFormat.parse(dateString);
                    } catch (ParseException e) {
                        throw new ApplicationException(e);
                    }
                    return fromDate;
                }).map(date -> {
                    List<Date> callDates = new ArrayList<>();
                    try {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        while (!calendar.getTime().after(simpleDateFormat
                                .parse(Optional.ofNullable(seatAvailabilityRequestDto.getToDate())
                                        .orElseThrow(() -> new IllegalArgumentException("Invalid To Date"))))) {
                            callDates.add(calendar.getTime());
                            calendar.add(Calendar.DATE, 6);
                        }
                    } catch (ParseException e) {
                        throw new ApplicationException(e);
                    }
                    return callDates;
                }).orElseThrow(() -> new IllegalStateException("Exception while transforming dates"));
    }
}
