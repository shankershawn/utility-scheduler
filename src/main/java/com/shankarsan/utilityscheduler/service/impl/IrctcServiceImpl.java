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

import java.net.URI;

@Service
@RequiredArgsConstructor
public class IrctcServiceImpl implements IrctcService {

    private final RestTemplate restTemplate;

    @Override
    public SeatAvailabilityResponseDto fetchAvailabilityData(SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
        ResponseEntity<SeatAvailabilityResponseDto> responseEntity = restTemplate.exchange(RequestEntity
                .post(URI.create("https://www.irctc.co.in/eticketing/protected/mapps1/avlFarenquiry/12891/20230619/KUR/PURI/2S/GN/N"))
                .header("greq", "1686997853813")
                .contentType(MediaType.APPLICATION_JSON)
                .body(seatAvailabilityRequestDto), SeatAvailabilityResponseDto.class);
        return responseEntity.getBody();
    }
}
