package com.shankarsan.utilityscheduler.service.impl;

import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.service.IrctcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
                .format("/avlFarenquiry/%s/%s/%s/%s/%s/%s/N",
                        seatAvailabilityRequestDto.getTrainNumber(),
                        seatAvailabilityRequestDto.getJourneyDate(),
                        seatAvailabilityRequestDto.getFromStnCode(),
                        seatAvailabilityRequestDto.getToStnCode(),
                        seatAvailabilityRequestDto.getClassCode().getName(),
                        seatAvailabilityRequestDto.getQuotaCode().name());
        ResponseEntity<SeatAvailabilityResponseDto> responseEntity = irctcRestTemplate.exchange(RequestEntity
                .post(url)
                .header("greq", String.valueOf(System.currentTimeMillis()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(seatAvailabilityRequestDto), SeatAvailabilityResponseDto.class);
        return responseEntity.getBody();
    }
}
