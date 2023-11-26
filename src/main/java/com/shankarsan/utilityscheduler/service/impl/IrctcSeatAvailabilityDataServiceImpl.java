package com.shankarsan.utilityscheduler.service.impl;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.service.SeatAvailabilityDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Profile(CommonConstants.IRCTC)
public class IrctcSeatAvailabilityDataServiceImpl implements SeatAvailabilityDataService {

    private final RestTemplate irctcRestTemplate;

    @Override
    public SeatAvailabilityResponseDto fetchAvailabilityData(SeatAvailabilityRequestDto seatAvailabilityRequestDto) {
        String url = String
                .format("/eticketing/protected/mapps1/avlFarenquiry/%s/%s/%s/%s/%s/%s/N",
                        seatAvailabilityRequestDto.getTrainNumber(),
                        seatAvailabilityRequestDto.getFromDate(),
                        seatAvailabilityRequestDto.getFromStnCode(),
                        seatAvailabilityRequestDto.getToStnCode(),
                        seatAvailabilityRequestDto.getClassCode().getName(),
                        seatAvailabilityRequestDto.getQuotaCode().name()
                );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAll(
                Map.of("sec-ch-ua", "\"Google Chrome\";v=\"119\", \"Chromium\";v=\"119\", \"Not?A_Brand\";v=\"24\"",
                        "sec-ch-ua-mobile", "?0",
                        "User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
                        "greq", "1699539245708",
                        "Content-Type", "application/json; charset=UTF-8",
                        "Content-Language", "en",
                        "Accept", "application/json, text/plain, */*",
                        "bmirak", "webbm",
                        "Referer", "https://www.irctc.co.in/nget/booking/train-list",
                        "sec-ch-ua-platform", "\"macOS\"")
        );
        ResponseEntity<SeatAvailabilityResponseDto> responseEntity = irctcRestTemplate
                .exchange(RequestEntity.<SeatAvailabilityRequestDto>post(url).headers(httpHeaders).body(seatAvailabilityRequestDto),
                        SeatAvailabilityResponseDto.class);
        return responseEntity.getBody();
    }
}
