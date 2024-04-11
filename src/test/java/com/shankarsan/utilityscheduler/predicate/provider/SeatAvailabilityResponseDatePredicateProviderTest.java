package com.shankarsan.utilityscheduler.predicate.provider;

import com.shankarsan.utilityscheduler.dto.AvailabilityDayDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
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
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatAvailabilityResponseDatePredicateProviderTest {

    @Mock
    private SeatAvailabilityDateParser seatAvailabilityDateParser;
    @InjectMocks
    private SeatAvailabilityResponseDatePredicateProvider seatAvailabilityResponseDatePredicateProvider;

    @ParameterizedTest
    @CsvSource({
            "01-02-2025,02-02-2025,false",
            "01-02-2025,01-02-2025,true",
            "01-02-2025,31-01-2025,true"})
    void shouldFilterSeatAvailabilityResponseDate(String toDateString, String availabilityDateString, Boolean result) {
        SeatAvailabilityResponseDto seatAvailabilityResponseDto =
                getSeatAvailabilityResponseDto(toDateString, availabilityDateString);
        Calendar calendar = Calendar.getInstance();
        Integer[] toDateArray = Arrays.stream(toDateString.split("-"))
                .map(Integer::valueOf).toArray(Integer[]::new);
        calendar.set(toDateArray[2], toDateArray[1] - 1, toDateArray[0]);
        Date toDate = calendar.getTime();
        when(seatAvailabilityDateParser.parse(toDateString)).thenReturn(toDate);
        Integer[] availabilityDateArray = Arrays.stream(availabilityDateString.split("-"))
                .map(Integer::valueOf).toArray(Integer[]::new);
        calendar.set(availabilityDateArray[2], availabilityDateArray[1] - 1, availabilityDateArray[0]);
        Date availabilityDate = calendar.getTime();
        when(seatAvailabilityDateParser.parse(availabilityDateString)).thenReturn(availabilityDate);
        Predicate<AvailabilityDayDto> availabilityDayDtoPredicate = seatAvailabilityResponseDatePredicateProvider
                .getAvailabilityDayDtoPredicate(seatAvailabilityResponseDto);
        assertEquals(result, availabilityDayDtoPredicate.test(seatAvailabilityResponseDto.getAvlDayList().get(0)));
    }

    private static SeatAvailabilityResponseDto getSeatAvailabilityResponseDto(String toDate, String availabilityDate) {
        return SeatAvailabilityResponseDto.builder()
                .seatAvailabilityRequestDto(SeatAvailabilityRequestDto.builder()
                        .toDate(toDate)
                        .build())
                .avlDayList(List.of(AvailabilityDayDto.builder()
                        .availabilityDate(availabilityDate)
                        .wlType("wlType")
                        .availabilityType("availabilityType")
                        .reasonType("reasonType")
                        .currentBkgFlag("currentBkgFlag")
                        .availabilityStatus("availabilityStatus")
                        .build()))
                .build();
    }
}
