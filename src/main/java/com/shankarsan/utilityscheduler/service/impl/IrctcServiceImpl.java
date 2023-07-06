package com.shankarsan.utilityscheduler.service.impl;

import com.shankarsan.utilityscheduler.configuration.ApplicationConfiguration;
import com.shankarsan.utilityscheduler.dto.EmailDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.service.IrctcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IrctcServiceImpl implements IrctcService {

    private final RestTemplate irctcRestTemplate;

    private final ApplicationConfiguration applicationConfiguration;

    @Override
    public SeatAvailabilityResponseDto fetchAvailabilityData(SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
        SeatAvailabilityResponseDto seatAvailabilityResponseDto;
        List<EmailDto> emailDtoList = seatAvailabilityRequestDto.getEmailDtoList();
        seatAvailabilityRequestDto.setEmailDtoList(null);
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
                .headers(httpHeaders -> {
                    httpHeaders.set("greq", String.valueOf(System.currentTimeMillis()));
                    applicationConfiguration.getHeaderMap()
                            .forEach(httpHeaders::set);
                })
                .contentType(MediaType.APPLICATION_JSON)
                .body(seatAvailabilityRequestDto), SeatAvailabilityResponseDto.class);
        seatAvailabilityResponseDto = responseEntity.getBody();
        Optional.ofNullable(seatAvailabilityResponseDto)
                .ifPresent(seatAvailabilityResponseDto1 -> {
                    seatAvailabilityResponseDto.setEmailDtoList(emailDtoList);
                    seatAvailabilityResponseDto.setMailSubject(new StringBuilder("Availability for ")
                            .append(seatAvailabilityRequestDto.getTrainNumber())
                            .append(" on ").append(seatAvailabilityRequestDto.getJourneyDate())
                            .append(" in class ").append(seatAvailabilityRequestDto.getClassCode().getName())
                            .append(" quota ").append(seatAvailabilityRequestDto.getQuotaCode().name())
                            .toString());
                });

        return seatAvailabilityResponseDto;
    }
}
