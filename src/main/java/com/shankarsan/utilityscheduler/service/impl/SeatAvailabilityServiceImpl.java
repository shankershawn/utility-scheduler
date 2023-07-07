package com.shankarsan.utilityscheduler.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.dto.EmailDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.service.DropboxService;
import com.shankarsan.utilityscheduler.service.IrctcService;
import com.shankarsan.utilityscheduler.service.SeatAvailabilityService;
import com.shankarsan.utilityscheduler.service.comms.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class SeatAvailabilityServiceImpl implements SeatAvailabilityService {
    private final IrctcService irctcService;

    private final DropboxService dropboxService;

    private final MailService mailService;

    private final CacheManager caffeineCacheManager;

    public void processSeatAvailability() {
        try {
            File downloadedFile = dropboxService.downloadFile(CommonConstants.SEAT_AVAILABILITY_CSV);
            transformInputStream(new FileInputStream(downloadedFile))
                    .stream()
                    .map(this::invokeIrctcService)
                    .map(this::logSeatAvailability)
                    .forEach(this::mailResponse);
        } catch (Exception e) {
            log.error("Exception encountere", e);
        }
    }

    private SeatAvailabilityResponseDto invokeIrctcService(SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
        SeatAvailabilityResponseDto seatAvailabilityResponseDto = irctcService
                .fetchAvailabilityData(seatAvailabilityRequestDto);
        processMailEligibility(seatAvailabilityRequestDto, seatAvailabilityResponseDto);
        return seatAvailabilityResponseDto;
    }

    private SeatAvailabilityResponseDto logSeatAvailability(SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        Optional.ofNullable(seatAvailabilityResponseDto.getAvlDayList())
                .map(List::toString)
                .ifPresent(dataList -> log.debug("Data: {}", dataList));
        Optional.ofNullable(seatAvailabilityResponseDto.getErrorMessage())
                .ifPresent(log::debug);
        return seatAvailabilityResponseDto;
    }

    private void mailResponse(SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        Optional.ofNullable(seatAvailabilityResponseDto).map(SeatAvailabilityResponseDto::getEmailDtoList)
                .ifPresent(list -> {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    mailService.sendMail(list, Optional.ofNullable(seatAvailabilityResponseDto.getAvlDayList())
                                    .map(gson::toJson)
                                    .orElse(seatAvailabilityResponseDto.getErrorMessage()),
                            seatAvailabilityResponseDto.getMailSubject(), null);
                });
    }

    private List<SeatAvailabilityRequestDto> transformInputStream(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return bufferedReader.lines()
                .map(line -> {
                    String[] lineArray = line.split(CommonConstants.COMMA);
                    return SeatAvailabilityRequestDto.builder()
                            .trainNumber(lineArray[0])
                            .classCode(SeatAvailabilityRequestDto.ClassCode.valueOf(lineArray[1]))
                            .quotaCode(SeatAvailabilityRequestDto.QuotaCode.valueOf(lineArray[2]))
                            .fromStnCode(lineArray[3])
                            .toStnCode(lineArray[4])
                            .journeyDate(lineArray[5])
                            .emailDtoList(Arrays.stream(lineArray[6]
                                            .split(CommonConstants.TILDE))
                                    .map(e -> EmailDto.builder()
                                            .emailAddress(e)
                                            .build())
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private void processMailEligibility(final SeatAvailabilityRequestDto seatAvailabilityRequestDto,
                                        final SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        Cache cache = caffeineCacheManager.getCache("availabilityCache");
        Optional.ofNullable(cache)
                .map(cache1 -> cache1.get(getCacheKey(seatAvailabilityRequestDto)))
                .map(Cache.ValueWrapper::get)
                .map(e -> ((SeatAvailabilityResponseDto) e).getAvlDayList())
                .ifPresentOrElse(cachedAvailabilityData ->
                        Optional.ofNullable(seatAvailabilityResponseDto)
                                .map(SeatAvailabilityResponseDto::getAvlDayList)
                                .ifPresent(fetchedAvailabilityData -> {
                                    if (!cachedAvailabilityData.equals(fetchedAvailabilityData)) {
                                        cache.put(getCacheKey(seatAvailabilityRequestDto), seatAvailabilityResponseDto);
                                        setMailParams(seatAvailabilityRequestDto, seatAvailabilityResponseDto,
                                                Boolean.TRUE);
                                    }
                                }), () -> {
                    cache.put(getCacheKey(seatAvailabilityRequestDto), seatAvailabilityResponseDto);
                    setMailParams(seatAvailabilityRequestDto, seatAvailabilityResponseDto, Boolean.FALSE);
                });
    }

    private String getCacheKey(SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
        return Optional.ofNullable(seatAvailabilityRequestDto)
                .map(seatAvailabilityRequestDto1 ->
                        new StringBuilder(seatAvailabilityRequestDto1.getTrainNumber())
                                .append(seatAvailabilityRequestDto1.getClassCode().getName())
                                .append(seatAvailabilityRequestDto1.getQuotaCode().name())
                                .append(seatAvailabilityRequestDto1.getJourneyDate())
                                .append(seatAvailabilityRequestDto1.getFromStnCode())
                                .append(seatAvailabilityRequestDto1.getToStnCode()).toString())
                .orElseThrow(() -> new IllegalArgumentException("Invalid seatAvailabilityRequestDto"));
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
                .append(" ").append(seatAvailabilityResponseDto.getTrainName())
                .append(" on ").append(seatAvailabilityRequestDto.getJourneyDate())
                .append(" from ").append(seatAvailabilityRequestDto.getFromStnCode())
                .append(" to ").append(seatAvailabilityRequestDto.getToStnCode())
                .append(" in class ")
                .append(seatAvailabilityRequestDto.getClassCode().getName())
                .append(" quota ").append(seatAvailabilityRequestDto.getQuotaCode().name())
                .toString());
    }
}
