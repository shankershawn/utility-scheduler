package com.shankarsan.seat.availability.service.impl;

import com.shankarsan.seat.availability.constants.CommonConstants;
import com.shankarsan.seat.availability.dto.SeatAvailabilityRequestDto;
import com.shankarsan.seat.availability.dto.SeatAvailabilityResponseDto;
import com.shankarsan.seat.availability.service.SeatAvailabilityDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service(CommonConstants.CONFIRM_TKT)
@RequiredArgsConstructor
public class ConfirmTktSeatAvailabilityDataServiceImpl implements SeatAvailabilityDataService {

  @Qualifier(CommonConstants.CONFIRM_TKT + "Template")
  private final RestTemplate confirmTktRestTemplate;

  @Override
  public SeatAvailabilityResponseDto fetchAvailabilityData(SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
    String url = String
        .format("/api/platform/trainbooking/avlFareenquiry?trainNo=%s&travelClass=%s&quota=%s&fromStnCode=%s"
                + "&destStnCode=%s&doj=%s"
                + "&token=204F97FDBEBA275624E386BD688AE83E94E87D37364787DC5AD51D3C05E47F58"
                + "&planZeroCan=RO-E1",
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
