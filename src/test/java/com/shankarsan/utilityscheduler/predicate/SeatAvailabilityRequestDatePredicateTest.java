package com.shankarsan.utilityscheduler.predicate;

import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.parser.SeatAvailabilityDateParser;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatAvailabilityRequestDatePredicateTest {

    @InjectMocks
    private SeatAvailabilityRequestDatePredicate seatAvailabilityRequestDatePredicate;

    @Mock
    private SeatAvailabilityDateParser seatAvailabilityDateParser;

    @ParameterizedTest
    @CsvSource({
            "01-02-2025,02-02-2025,true",
            "01-02-2025,01-02-2025,true",
            "01-02-2025,31-01-2025,false"})
    void shouldFilterSeatAvailabilityRequestDate(String fromDateString, String toDateString, Boolean result) {
        SeatAvailabilityRequestDto seatAvailabilityRequestDto =
                getSeatAvailabilityRequestDto(fromDateString, toDateString);
        Calendar calendar = Calendar.getInstance();
        Integer[] toDateArray = Arrays.stream(toDateString.split("-"))
                .map(Integer::valueOf).toArray(Integer[]::new);
        calendar.set(toDateArray[2], toDateArray[1] - 1, toDateArray[0]);
        Date toDate = calendar.getTime();
        when(seatAvailabilityDateParser.parse(toDateString)).thenReturn(toDate);
        Integer[] fromDateArray = Arrays.stream(fromDateString.split("-"))
                .map(Integer::valueOf).toArray(Integer[]::new);
        calendar.set(fromDateArray[2], fromDateArray[1] - 1, fromDateArray[0]);
        Date fromDate = calendar.getTime();
        when(seatAvailabilityDateParser.format(any(Date.class))).thenReturn("01-02-2025");
        when(seatAvailabilityDateParser.parse(fromDateString)).thenReturn(fromDate);
        assertEquals(result, seatAvailabilityRequestDatePredicate.test(seatAvailabilityRequestDto));
    }

    private SeatAvailabilityRequestDto getSeatAvailabilityRequestDto(String fromDateString, String toDateString) {
        return SeatAvailabilityRequestDto.builder()
                .toDate(toDateString)
                .fromDate(fromDateString)
                .build();
    }

}
