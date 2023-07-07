package com.shankarsan.utilityscheduler.service.impl;

import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.service.IrctcService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@EnableCaching
public class IrctcServiceImpl implements IrctcService {

    private final RestTemplate irctcRestTemplate;

    private final CacheManager caffeineCacheManager;

    @Override
    public SeatAvailabilityResponseDto fetchAvailabilityData(SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
        SeatAvailabilityResponseDto seatAvailabilityResponseDto;
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
        seatAvailabilityResponseDto = responseEntity.getBody();
        processMailEligibility(seatAvailabilityRequestDto, seatAvailabilityResponseDto);
        return seatAvailabilityResponseDto;
    }

    private void processMailEligibility(SeatAvailabilityRequestDto seatAvailabilityRequestDto,
                                        final SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        Cache cache = caffeineCacheManager.getCache("availabilityCache");
        Optional.ofNullable(cache)
                .map(cache1 -> cache1.get(seatAvailabilityRequestDto))
                .map(Cache.ValueWrapper::get)
                .map(e -> ((SeatAvailabilityResponseDto) e).getAvlDayList())
                .ifPresentOrElse(cachedAvailabilityData ->
                        Optional.ofNullable(seatAvailabilityResponseDto)
                                .map(SeatAvailabilityResponseDto::getAvlDayList)
                                .ifPresent(fetchedAvailabilityData -> {
                                    if (!cachedAvailabilityData.equals(fetchedAvailabilityData)) {
                                        cache.put(seatAvailabilityRequestDto, seatAvailabilityResponseDto);
                                        setMailParams(seatAvailabilityRequestDto, seatAvailabilityResponseDto,
                                                Boolean.TRUE);
                                    }
                                }), () -> {
                    cache.put(seatAvailabilityRequestDto, seatAvailabilityResponseDto);
                    setMailParams(seatAvailabilityRequestDto, seatAvailabilityResponseDto, Boolean.FALSE);
                });
    }

    private static void setMailParams(SeatAvailabilityRequestDto seatAvailabilityRequestDto,
                                      SeatAvailabilityResponseDto seatAvailabilityResponseDto, boolean replyFlag) {
        StringBuilder subjectBuilder;
        if (replyFlag) {
            subjectBuilder = new StringBuilder("Re: ");
        } else {
            subjectBuilder = new StringBuilder();
        }
        seatAvailabilityResponseDto
                .setEmailDtoList(seatAvailabilityRequestDto.getEmailDtoList());
        seatAvailabilityResponseDto.setMailSubject(subjectBuilder.append("Availability for ")
                .append(seatAvailabilityRequestDto.getTrainNumber())
                .append(" on ").append(seatAvailabilityRequestDto.getJourneyDate())
                .append(" in class ")
                .append(seatAvailabilityRequestDto.getClassCode().getName())
                .append(" quota ").append(seatAvailabilityRequestDto.getQuotaCode().name())
                .toString());
    }
}
