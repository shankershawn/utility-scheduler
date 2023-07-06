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
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatAvailabilityServiceImpl implements SeatAvailabilityService {
    private final IrctcService irctcService;

    private final DropboxService dropboxService;

    private final MailService mailService;

    public void processSeatAvailability() {
        File downloadedFile = dropboxService.downloadFile(CommonConstants.SEAT_AVAILABILITY_CSV);
        try {
            transformInputStream(new FileInputStream(downloadedFile))
                    .stream()
                    .map(irctcService::fetchAvailabilityData)
                    .map(this::logSeatAvailability)
                    .forEach(this::mailResponse);
        } catch (FileNotFoundException e) {
            log.error("Exception encountere", e);
        }
    }

    private SeatAvailabilityResponseDto logSeatAvailability(SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        Optional.ofNullable(seatAvailabilityResponseDto.getAvlDayList())
                .map(List::toString)
                .ifPresent(dataList -> log.info("Data: {}", dataList));
        Optional.ofNullable(seatAvailabilityResponseDto.getErrorMessage())
                .ifPresent(log::info);
        return seatAvailabilityResponseDto;
    }

    private void mailResponse(SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        mailService.sendMail(seatAvailabilityResponseDto.getEmailDtoList(),
                Optional.ofNullable(seatAvailabilityResponseDto.getAvlDayList())
                        .map(gson::toJson)
                        .orElse(seatAvailabilityResponseDto.getErrorMessage()),
                seatAvailabilityResponseDto.getMailSubject(), null);
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
}
