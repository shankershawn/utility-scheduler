package com.shankarsan.utilityscheduler.scheduler;

import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.service.IrctcService;
import com.shankarsan.utilityscheduler.service.impl.IrctcServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class SeatAvailabilityScheduler {

    private final IrctcService irctcService;


    @Scheduled(cron = "* * * * * *")
    public void execute() {
        SeatAvailabilityResponseDto seatAvailabilityResponseDto = irctcService
                .fetchAvailabilityData(SeatAvailabilityRequestDto.builder()
                        .classCode(SeatAvailabilityRequestDto.ClassCode._2S)
                        .fromStnCode("KUR")
                        .toStnCode("PURI")
                        .journeyDate("20230701")
                        .quotaCode(SeatAvailabilityRequestDto.QuotaCode.GN)
                        .trainNumber("12891")
                        .build()
                );
        System.out.println(seatAvailabilityResponseDto.getAvlDayList());

    }
}
