package com.shankarsan.utilityscheduler.service.impl;

import com.shankarsan.dropbox.service.DropboxWebhookService;
import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.consumer.SeatAvailabilityEmailProcessor;
import com.shankarsan.utilityscheduler.dto.AvailabilityDayDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.parser.SeatAvailabilityDateParser;
import com.shankarsan.utilityscheduler.predicate.SeatAvailabilityRequestDatePredicate;
import com.shankarsan.utilityscheduler.predicate.provider.SeatAvailabilityResponseDatePredicateProvider;
import com.shankarsan.utilityscheduler.service.SeatAvailabilityDataService;
import com.shankarsan.utilityscheduler.service.SeatAvailabilityService;
import com.shankarsan.utilityscheduler.transformers.SeatAvailabilityInputStreamTransformer;
import com.shankarsan.utilityscheduler.transformers.SeatAvailabilityRequestDateTransformer;
import com.shankarsan.utilityscheduler.transformers.SeatAvailabilityResponseFlattenTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class SeatAvailabilityServiceImpl implements SeatAvailabilityService {

    @Qualifier(CommonConstants.IRCTC)
    private final SeatAvailabilityDataService irctcSeatAvailabilityDataServiceImpl;

    @Qualifier(CommonConstants.CONFIRM_TKT)
    private final SeatAvailabilityDataService confirmTktSeatAvailabilityDataServiceImpl;

    private final DropboxWebhookService dropboxWebhookService;

    private final CacheManager cacheManager;

    private final SeatAvailabilityRequestDateTransformer seatAvailabilityRequestDateTransformer;

    private final SeatAvailabilityInputStreamTransformer seatAvailabilityInputStreamTransformer;

    private final SeatAvailabilityResponseFlattenTransformer seatAvailabilityResponseFlattenTransformer;

    private final SeatAvailabilityEmailProcessor seatAvailabilityEmailProcessor;

    private final SeatAvailabilityDateParser seatAvailabilityDateParser;

    private final SeatAvailabilityResponseDatePredicateProvider seatAvailabilityResponseDatePredicateProvider;

    private final SeatAvailabilityRequestDatePredicate seatAvailabilityRequestDatePredicate;

    private final RetryTemplate retryTemplate;

    @Transactional
    public void processSeatAvailability() {
        try {
            File seatAvailabilityFileData = Optional.ofNullable(cacheManager)
                    .map(cacheManager1 -> cacheManager1.getCache(CommonConstants.DROPBOX_AVAILABILITY_FILE_CACHE))
                    .map(cache -> cache.get(CommonConstants.SEAT_AVAILABILITY_FILE_DATA))
                    .map(Cache.ValueWrapper::get)
                    .map(e -> (File) e)
                    .orElseGet(dropboxWebhookService::refreshAvailabilityFileData);

            seatAvailabilityInputStreamTransformer.apply(new FileInputStream(seatAvailabilityFileData)).stream()
                    //TODO .map(this::publishRequestToKafka)
                    //TODO perform below
                    .map(this::invokeSeatAvailabilityDataService)
                    .map(seatAvailabilityResponseFlattenTransformer)
                    .map(this::filterRepeatedAvailabilityDates)
                    .map(this::applySeatAvailabilityResponseDateFilter)
                    .map(this::logSeatAvailability)
                    .forEach(seatAvailabilityEmailProcessor);
        } catch (FileNotFoundException fnfe) {
            log.error("Exception encountered", fnfe);
            dropboxWebhookService.refreshAvailabilityFileData();
        } catch (Exception e) {
            log.error("Exception encountered", e);
        }
    }

    private SeatAvailabilityResponseDto filterRepeatedAvailabilityDates(
            SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        final List<AvailabilityDayDto> availabilityDayDtos = Optional.ofNullable(seatAvailabilityResponseDto)
                .map(SeatAvailabilityResponseDto::getAvlDayList)
                .orElseThrow(() -> new IllegalStateException("availabilityDayDtos is null"));

        final Set<AvailabilityDayDto> availabilityDayDtos1 = new LinkedHashSet<>();
        availabilityDayDtos1.addAll(availabilityDayDtos);
        availabilityDayDtos.clear();
        availabilityDayDtos.addAll(availabilityDayDtos1);
        return seatAvailabilityResponseDto;
    }

    private SeatAvailabilityResponseDto applySeatAvailabilityResponseDateFilter(
            SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        Predicate<AvailabilityDayDto> dateFilter = Optional.ofNullable(seatAvailabilityResponseDto)
                .map(seatAvailabilityResponseDatePredicateProvider::getAvailabilityDayDtoPredicate)
                .orElseThrow(() -> new IllegalStateException("Filter predicate not found"));

        Optional.of(seatAvailabilityResponseDto)
                .map(SeatAvailabilityResponseDto::getAvlDayList)
                .map(Collection::stream)
                .map(availabilityDayDtoStream -> availabilityDayDtoStream
                        .filter(dateFilter)
                        .collect(Collectors.toList()))
                .ifPresent(seatAvailabilityResponseDto::setAvlDayList);
        return seatAvailabilityResponseDto;
    }

    private List<SeatAvailabilityResponseDto> invokeSeatAvailabilityDataService(
            SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
        final String originalFromDate = seatAvailabilityRequestDto.getFromDate();
        List<SeatAvailabilityResponseDto> seatAvailabilityResponseDtos = seatAvailabilityRequestDateTransformer
                .apply(seatAvailabilityRequestDto).stream()
                .map(date -> {
                    seatAvailabilityRequestDto.setFromDate(seatAvailabilityDateParser.format(date),
                            Boolean.FALSE);
                    return seatAvailabilityRequestDto;
                })
                .filter(seatAvailabilityRequestDatePredicate)
                .map(seatAvailabilityRequestDto1 -> {
                    seatAvailabilityRequestDto1
                            .setFromDate(seatAvailabilityDateParser
                                            .format(seatAvailabilityDateParser
                                                    .parse(seatAvailabilityRequestDto1.getFromDate())),
                                    Boolean.TRUE);
                    SeatAvailabilityResponseDto seatAvailabilityResponseDto =
                            retryTemplate.execute(retryContext -> {
                                        log.info("Retrying irctc call. Retry count is {}",
                                                retryContext.getRetryCount());
                                        return irctcSeatAvailabilityDataServiceImpl
                                                .fetchAvailabilityData(seatAvailabilityRequestDto1);
                                    },
                                    retryContext -> {
                                        log.info("Falling back to confirm tkt irctc call. Retry count is {}",
                                                retryContext.getRetryCount());
                                        return confirmTktSeatAvailabilityDataServiceImpl
                                                .fetchAvailabilityData(seatAvailabilityRequestDto1);
                                    });
                    seatAvailabilityResponseDto.setSeatAvailabilityRequestDto(seatAvailabilityRequestDto1);
                    return seatAvailabilityResponseDto;
                })
                .collect(Collectors.toList());
        seatAvailabilityRequestDto.setFromDate(originalFromDate, Boolean.TRUE);
        return seatAvailabilityResponseDtos;
    }

    private SeatAvailabilityResponseDto logSeatAvailability(SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        Optional.ofNullable(seatAvailabilityResponseDto.getAvlDayList())
                .map(List::toString)
                .ifPresent(dataList -> log.debug("Data: {}", dataList));
        Optional.ofNullable(seatAvailabilityResponseDto.getErrorMessage())
                .ifPresent(log::debug);
        return seatAvailabilityResponseDto;
    }


}
