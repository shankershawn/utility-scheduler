package com.shankarsan.utilityscheduler.consumer;

import com.google.gson.Gson;
import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.dto.AvailabilityDayDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.filter.SeatAvailabilityDateFilter;
import com.shankarsan.utilityscheduler.service.comms.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatAvailabilityEmailProcessor implements Consumer<SeatAvailabilityResponseDto> {

    private final CacheManager cacheManager;

    private final MailService mailService;

    private final Gson gsonBean;

    private final SeatAvailabilityDateFilter seatAvailabilityDateFilter;

    @Override
    public void accept(SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        processMailEligibility(seatAvailabilityResponseDto);
        mailSeatAvailabilityData(seatAvailabilityResponseDto);
    }

    private void mailSeatAvailabilityData(SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        Predicate<AvailabilityDayDto> dateFilter = Optional.ofNullable(seatAvailabilityResponseDto)
                .map(seatAvailabilityDateFilter::filterSeatAvailabilityData)
                .orElseThrow(() -> new IllegalStateException("Filter predicate not found"));
        Optional.ofNullable(seatAvailabilityResponseDto).map(SeatAvailabilityResponseDto::getEmailDtoList)
                .ifPresent(list ->
                        mailService.sendMail(list, Optional.ofNullable(seatAvailabilityResponseDto.getAvlDayList())
                                        .map(Collection::stream)
                                        .map(availabilityDayDtoStream -> availabilityDayDtoStream
                                                .filter(dateFilter)
                                                .collect(Collectors.toList()))
                                        .map(gsonBean::toJson)
                                        .orElse(seatAvailabilityResponseDto.getErrorMessage()),
                                seatAvailabilityResponseDto.getMailSubject(), null)
                );
    }

    private void processMailEligibility(final SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        Cache cache = cacheManager.getCache(CommonConstants.AVAILABILITY_CACHE);
        Optional.ofNullable(cache)
                .map(cache1 -> cache1.get(getCacheKey(seatAvailabilityResponseDto.getSeatAvailabilityRequestDto())))
                .map(Cache.ValueWrapper::get)
                .map(e -> ((SeatAvailabilityResponseDto) e).getAvlDayList())
                .ifPresentOrElse(cachedAvailabilityData ->
                        Optional.ofNullable(seatAvailabilityResponseDto)
                                .map(SeatAvailabilityResponseDto::getAvlDayList)
                                .ifPresent(fetchedAvailabilityData -> {
                                    if (!cachedAvailabilityData.equals(fetchedAvailabilityData)) {
                                        log.debug("Data changed:::Setting cache and sending email {}",
                                                seatAvailabilityResponseDto);
                                        Optional.ofNullable(cache)
                                                .ifPresent((cache1 -> cache1
                                                        .put(getCacheKey(seatAvailabilityResponseDto
                                                                        .getSeatAvailabilityRequestDto()),
                                                                seatAvailabilityResponseDto)));
                                        setMailParams(seatAvailabilityResponseDto
                                                        .getSeatAvailabilityRequestDto(), seatAvailabilityResponseDto,
                                                Boolean.TRUE);
                                    }
                                }), () ->
                        Optional.ofNullable(seatAvailabilityResponseDto)
                                .map(SeatAvailabilityResponseDto::getAvlDayList)
                                .ifPresent(dayDtos -> {
                                    log.debug("Availability data not found in cache:::" +
                                                    "Setting cache and sending email {}",
                                            seatAvailabilityResponseDto);
                                    Optional.ofNullable(cache)
                                            .ifPresent((cache1 -> cache1
                                                    .put(getCacheKey(seatAvailabilityResponseDto
                                                                    .getSeatAvailabilityRequestDto()),
                                                            seatAvailabilityResponseDto)));
                                    setMailParams(seatAvailabilityResponseDto
                                                    .getSeatAvailabilityRequestDto(),
                                            seatAvailabilityResponseDto, Boolean.FALSE);
                                })
                );
    }

    private String getCacheKey(SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
        return Optional.ofNullable(seatAvailabilityRequestDto)
                .map(seatAvailabilityRequestDto1 ->
                        new StringBuilder(seatAvailabilityRequestDto1.getTrainNumber())
                                .append(seatAvailabilityRequestDto1.getClassCode().getName())
                                .append(seatAvailabilityRequestDto1.getQuotaCode().name())
                                .append(seatAvailabilityRequestDto1.getFromDate())
                                .append(seatAvailabilityRequestDto1.getToDate())
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
                .append(" on ").append(seatAvailabilityRequestDto.getFromDate())
                .append(" to ").append(seatAvailabilityRequestDto.getToDate())
                .append(" from ").append(seatAvailabilityRequestDto.getFromStnCode())
                .append(" to ").append(seatAvailabilityRequestDto.getToStnCode())
                .append(" in class ")
                .append(seatAvailabilityRequestDto.getClassCode().getName())
                .append(" quota ").append(seatAvailabilityRequestDto.getQuotaCode().name())
                .toString());
    }
}
