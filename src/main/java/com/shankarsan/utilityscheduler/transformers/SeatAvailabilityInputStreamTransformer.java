package com.shankarsan.utilityscheduler.transformers;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.dto.EmailDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SeatAvailabilityInputStreamTransformer implements Function<InputStream, List<SeatAvailabilityRequestDto>> {

    @Override
    public List<SeatAvailabilityRequestDto> apply(InputStream inputStream) {
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
                            .fromDate(lineArray[5])
                            .toDate(lineArray[6])
                            .emailDtoList(Arrays.stream(lineArray[7]
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