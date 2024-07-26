package com.shankarsan.utilityscheduler.service.impl;

import com.shankarsan.utilityscheduler.dto.EmailDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfirmTktSeatAvailabilityDataServiceImplTest {

    @Mock
    private RestTemplate confirmTktRestTemplate;

    @InjectMocks
    private ConfirmTktSeatAvailabilityDataServiceImpl confirmTktSeatAvailabilityDataService;

    private static SeatAvailabilityRequestDto seatAvailabilityRequestDto;

    @BeforeAll
    static void setup() {
        seatAvailabilityRequestDto = SeatAvailabilityRequestDto.builder()
                .classCode(SeatAvailabilityRequestDto.ClassCode._1A)
                .emailDtoList(List.of(EmailDto.builder()
                        .emailAddressLevel(EmailDto.EmailAddressLevel.TO)
                        .emailAddress("abc@def.com")
                        .build()))
                .toDate("2020-02-02")
                .fromDate("2020-02-01")
                .fromStnCode("HWH")
                .journeyDate("2020-02-02")
                .quotaCode(SeatAvailabilityRequestDto.QuotaCode.LD)
                .runDays(List.of(1))
                .toStnCode("NDLS")
                .trainNumber("12345")
                .build();
    }

    @Test
    void shouldFetchAvailabilityData() {
        when(confirmTktRestTemplate.exchange(ArgumentMatchers.<RequestEntity<SeatAvailabilityResponseDto>>any(),
                eq(SeatAvailabilityResponseDto.class)))
                .thenReturn(ResponseEntity.of(Optional.of(SeatAvailabilityResponseDto.builder().build())));
        assertNotNull(confirmTktSeatAvailabilityDataService.fetchAvailabilityData(seatAvailabilityRequestDto));
    }
}
