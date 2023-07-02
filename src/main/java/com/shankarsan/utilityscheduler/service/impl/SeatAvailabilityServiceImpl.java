package com.shankarsan.utilityscheduler.service.impl;

import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.service.DropboxService;
import com.shankarsan.utilityscheduler.service.IrctcService;
import com.shankarsan.utilityscheduler.service.SeatAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatAvailabilityServiceImpl implements SeatAvailabilityService {

    private final IrctcService irctcService;

    private final DropboxService dropboxService;

    public void processSeatAvailability() {
        InputStream inputStream = dropboxService.downloadFile("/screenshots/screenshot 2014-05-19 23.44.56.png");
        SeatAvailabilityResponseDto seatAvailabilityResponseDto = irctcService
                .fetchAvailabilityData(SeatAvailabilityRequestDto.builder()
                        .classCode(SeatAvailabilityRequestDto.ClassCode._2A)
                        .fromStnCode("HWH")
                        .toStnCode("SC")
                        .journeyDate("20231028")
                        .quotaCode(SeatAvailabilityRequestDto.QuotaCode.GN)
                        .trainNumber("12703")
                        .build()
                );

        Optional.ofNullable(seatAvailabilityResponseDto.getAvlDayList())
                .map(List::toString)
                .ifPresent(dataList -> log.info("Data: {}", dataList));
        Optional.ofNullable(seatAvailabilityResponseDto.getErrorMessage())
                .ifPresent(log::info);

    }
}
