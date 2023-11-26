package com.shankarsan.utilityscheduler.service.impl;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.service.SeatAvailabilityDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Profile(CommonConstants.CONFIRM_TKT)
public class ConfirmTktSeatAvailabilityDataServiceImpl implements SeatAvailabilityDataService {

    private final RestTemplate confirmTktRestTemplate;

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
        ResponseEntity<SeatAvailabilityResponseDto> responseEntity = confirmTktRestTemplate.exchange(RequestEntity
                .get(url).build(), SeatAvailabilityResponseDto.class);
        return responseEntity.getBody();
    }
}
