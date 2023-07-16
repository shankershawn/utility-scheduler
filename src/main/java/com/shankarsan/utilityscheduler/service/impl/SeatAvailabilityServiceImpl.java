package com.shankarsan.utilityscheduler.service.impl;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.consumer.SeatAvailabilityEmailProcessor;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.parser.SeatAvailabilityDateParser;
import com.shankarsan.utilityscheduler.service.DropboxWebhookService;
import com.shankarsan.utilityscheduler.service.IrctcService;
import com.shankarsan.utilityscheduler.service.SeatAvailabilityService;
import com.shankarsan.utilityscheduler.transformers.SeatAvailabilityInputStreamTransformer;
import com.shankarsan.utilityscheduler.transformers.SeatAvailabilityRequestDateTransformer;
import com.shankarsan.utilityscheduler.transformers.SeatAvailabilityResponseFlattenTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class SeatAvailabilityServiceImpl implements SeatAvailabilityService {

    private final IrctcService irctcService;

    private final DropboxWebhookService dropboxWebhookService;

    private final CacheManager cacheManager;

    private final SeatAvailabilityRequestDateTransformer seatAvailabilityRequestDateTransformer;

    private final SeatAvailabilityInputStreamTransformer seatAvailabilityInputStreamTransformer;

    private final SeatAvailabilityResponseFlattenTransformer seatAvailabilityResponseFlattenTransformer;

    private final SeatAvailabilityEmailProcessor seatAvailabilityEmailProcessor;

    private final SeatAvailabilityDateParser seatAvailabilityDateParser;

    public void processSeatAvailability() {
        try {
            File seatAvailabilityFileData = Optional.ofNullable(cacheManager)
                    .map(cacheManager1 -> cacheManager1.getCache(CommonConstants.DROPBOX_AVAILABILITY_FILE_CACHE))
                    .map(cache -> cache.get(CommonConstants.SEAT_AVAILABILITY_FILE_DATA))
                    .map(Cache.ValueWrapper::get)
                    .map(e -> (File) e)
                    .orElseGet(dropboxWebhookService::refreshAvailabilityFileData);

            seatAvailabilityInputStreamTransformer.apply(new FileInputStream(seatAvailabilityFileData)).stream()
                    .map(this::invokeIrctcService)
                    .map(seatAvailabilityResponseFlattenTransformer)
                    .map(this::logSeatAvailability)
                    .forEach(seatAvailabilityEmailProcessor);
        } catch (FileNotFoundException fnfe) {
            log.error("Exception encountered", fnfe);
            dropboxWebhookService.refreshAvailabilityFileData();
        } catch (Exception e) {
            log.error("Exception encountered", e);
        }
    }

    private List<SeatAvailabilityResponseDto> invokeIrctcService(SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
        final String originalFromDate = seatAvailabilityRequestDto.getFromDate();
        List<SeatAvailabilityResponseDto> seatAvailabilityResponseDtos = seatAvailabilityRequestDateTransformer
                .apply(seatAvailabilityRequestDto).stream()
                .map(date -> {
                    seatAvailabilityRequestDto.setFromDate(seatAvailabilityDateParser.format(date));
                    SeatAvailabilityResponseDto seatAvailabilityResponseDto = irctcService
                            .fetchAvailabilityData(seatAvailabilityRequestDto);
                    seatAvailabilityResponseDto.setSeatAvailabilityRequestDto(seatAvailabilityRequestDto);
                    return seatAvailabilityResponseDto;
                })
                .collect(Collectors.toList());
        seatAvailabilityRequestDto.setFromDate(originalFromDate);
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
