package com.shankarsan.utilityscheduler.transformers;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.dto.EmailDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.filter.SeatAvailabilityRequestDateFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatAvailabilityInputStreamTransformer implements Function<InputStream, List<SeatAvailabilityRequestDto>> {

    private final SeatAvailabilityRequestDateFilter seatAvailabilityRequestDateFilter;

    @Override
    public List<SeatAvailabilityRequestDto> apply(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return bufferedReader.lines()
                .map(line -> {
                    String[] lineArray = line.split(CommonConstants.PIPE);
                    return SeatAvailabilityRequestDto.builder()
                            .trainNumber(lineArray[0])
                            .classCode(SeatAvailabilityRequestDto.ClassCode.valueOf(lineArray[1]))
                            .quotaCode(SeatAvailabilityRequestDto.QuotaCode.valueOf(lineArray[2]))
                            .fromStnCode(lineArray[3])
                            .toStnCode(lineArray[4])
                            .fromDate(lineArray[5])
                            .toDate(lineArray[6])
                            .emailDtoList(getEmailDtoList(lineArray))
                            .runDays(getRunDays(lineArray))
                            .build();
                })
                .filter(seatAvailabilityRequestDateFilter)
                .collect(Collectors.toList());
    }

    private List<Integer> getRunDays(String[] lineArray) {
        return Optional
                .ofNullable(lineArray)
                .map(lineArray1 -> lineArray1[10])
                .map(lineArray2 -> lineArray2.split(""))
                .map(lineArray3 -> Arrays.stream(lineArray3)
                        .map(Integer::valueOf)
                        .collect(Collectors.toList())
                ).orElseThrow(() -> new IllegalStateException("Something went wrong in getRunDays"));
    }

    private List<EmailDto> getEmailDtoList(String[] lineArray) {
        final List<EmailDto> emailDtoList = new ArrayList<>();
        populateEmailDtoList(lineArray[7], emailDtoList, EmailDto.EmailAddressLevel.TO);
        populateEmailDtoList(lineArray[8], emailDtoList, EmailDto.EmailAddressLevel.CC);
        populateEmailDtoList(lineArray[9], emailDtoList, EmailDto.EmailAddressLevel.BCC);
        return emailDtoList;
    }

    private static void populateEmailDtoList(String lineArrayItem, List<EmailDto> emailDtoList,
                                             EmailDto.EmailAddressLevel emailAddressLevel) {
        Arrays.stream(lineArrayItem
                        .split(CommonConstants.TILDE))
                .filter(Predicate.not(String::isBlank))
                .map(e -> EmailDto.builder()
                        .emailAddress(e)
                        .emailAddressLevel(emailAddressLevel)
                        .build())
                .forEach(emailDtoList::add);
    }
}
