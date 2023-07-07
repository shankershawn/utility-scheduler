package com.shankarsan.utilityscheduler.service.impl;

import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.service.IrctcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class IrctcServiceImpl implements IrctcService {

    private final RestTemplate irctcRestTemplate;

    @Override
    public SeatAvailabilityResponseDto fetchAvailabilityData(SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
        String url = String
                .format("/api/platform/trainbooking/avlFareenquiry?trainNo=%s&travelClass=%s&quota=%s&fromStnCode=%s" +
                                "&destStnCode=%s&doj=%s" +
                                "&token=204F97FDBEBA275624E386BD688AE83E94E87D37364787DC5AD51D3C05E47F58" +
                                "&planZeroCan=RO-E1",
                        seatAvailabilityRequestDto.getTrainNumber(),
                        seatAvailabilityRequestDto.getClassCode().getName(),
                        seatAvailabilityRequestDto.getQuotaCode().name(),
                        seatAvailabilityRequestDto.getFromStnCode(),
                        seatAvailabilityRequestDto.getToStnCode(),
                        seatAvailabilityRequestDto.getJourneyDate());
        ResponseEntity<SeatAvailabilityResponseDto> responseEntity = irctcRestTemplate.exchange(RequestEntity
                .get(url).build(), SeatAvailabilityResponseDto.class);
        return responseEntity.getBody();
    }
}
