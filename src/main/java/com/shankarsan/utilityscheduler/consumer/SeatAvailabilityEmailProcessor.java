package com.shankarsan.utilityscheduler.consumer;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.dto.AvailabilityDayDto;
import com.shankarsan.utilityscheduler.dto.EmailDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.exception.ApplicationException;
import com.shankarsan.utilityscheduler.service.comms.MailService;
import j2html.rendering.FlatHtml;
import j2html.rendering.TagBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatAvailabilityEmailProcessor implements Consumer<SeatAvailabilityResponseDto> {

    public static final String STYLE = "style";
    private final CacheManager cacheManager;

    @Qualifier("html")
    private final MailService mailService;

    @Override
    public void accept(SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        processMailEligibility(seatAvailabilityResponseDto);
        mailSeatAvailabilityData(seatAvailabilityResponseDto);
    }

    private String getHtmlEmail(List<AvailabilityDayDto> availabilityDayDtos) {
        FlatHtml<StringBuilder> stringBuilderFlatHtml = FlatHtml.inMemory();
        try {
            TagBuilder rowBuilder;
            stringBuilderFlatHtml.appendStartTag("html").completeTag();
            stringBuilderFlatHtml.appendStartTag("body").completeTag();
            stringBuilderFlatHtml.appendStartTag("table")
                    .appendAttribute(STYLE, "border: 1px solid black;")
                    .completeTag();
            stringBuilderFlatHtml.appendStartTag("tr")
                    .appendAttribute(STYLE, "background-color: #F7C8E0;")
                    .completeTag();
            stringBuilderFlatHtml.appendStartTag("th").completeTag();
            stringBuilderFlatHtml.appendUnescapedText("Date");
            stringBuilderFlatHtml.appendEndTag("th");
            stringBuilderFlatHtml.appendStartTag("th").completeTag();
            stringBuilderFlatHtml.appendUnescapedText("Availability");
            stringBuilderFlatHtml.appendEndTag("th");
            stringBuilderFlatHtml.appendEndTag("tr");
            for (AvailabilityDayDto availabilityDayDto : availabilityDayDtos) {
                rowBuilder = stringBuilderFlatHtml.appendStartTag("tr");
                if (availabilityDayDto.getAvailabilityStatus().contains("AVAILABLE")) {
                    rowBuilder.appendAttribute(STYLE, "background-color: #B9F3E4;");
                } else if (availabilityDayDto.getAvailabilityStatus().contains("RAC")) {
                    rowBuilder.appendAttribute(STYLE, "background-color: #FFFEC4;");
                } else if (availabilityDayDto.getAvailabilityStatus().contains("WL")) {
                    rowBuilder.appendAttribute(STYLE, "background-color: #FF9B9B;");
                }
                rowBuilder.completeTag();
                stringBuilderFlatHtml.appendStartTag("td").completeTag();
                stringBuilderFlatHtml.appendUnescapedText(availabilityDayDto.getAvailabilityDate());
                stringBuilderFlatHtml.appendEndTag("td");
                stringBuilderFlatHtml.appendStartTag("td").completeTag();
                stringBuilderFlatHtml.appendUnescapedText(availabilityDayDto.getAvailabilityStatus());
                stringBuilderFlatHtml.appendEndTag("td");
                stringBuilderFlatHtml.appendEndTag("tr");
            }
            stringBuilderFlatHtml.appendEndTag("table");
            stringBuilderFlatHtml.appendEndTag("body");
            stringBuilderFlatHtml.appendEndTag("html");
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
        return stringBuilderFlatHtml.output().toString();
    }

    private void mailSeatAvailabilityData(SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        Optional.ofNullable(seatAvailabilityResponseDto).map(SeatAvailabilityResponseDto::getEmailDtoList)
                .ifPresent(list ->
                        mailService.sendMail(list, Optional.ofNullable(seatAvailabilityResponseDto.getAvlDayList())
                                        .map(this::getHtmlEmail)
                                        .orElse(seatAvailabilityResponseDto.getErrorMessage()),
                                seatAvailabilityResponseDto.getMailSubject(), null)
                );
    }

    private void processMailEligibility(final SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
        Optional<String> errorMessageOptional = Optional.ofNullable(seatAvailabilityResponseDto)
                .map(SeatAvailabilityResponseDto::getErrorMessage);
        if (errorMessageOptional.isPresent()) {
            log.debug("Error message found: {}. Returning", errorMessageOptional.get());
            return;
        }
        Cache cache = cacheManager.getCache(CommonConstants.AVAILABILITY_CACHE);
        SeatAvailabilityRequestDto seatAvailabilityRequestDto = Optional.ofNullable(seatAvailabilityResponseDto)
                .map(SeatAvailabilityResponseDto::getSeatAvailabilityRequestDto)
                .orElseThrow(() -> new IllegalStateException("Invalid seatAvailabilityRequestDto"));
        Optional.ofNullable(cache)
                .map(cache1 -> cache1.get(getCacheKey(seatAvailabilityRequestDto)))
                .map(Cache.ValueWrapper::get)
                .map(e -> ((SeatAvailabilityResponseDto) e).getAvlDayList())
                .ifPresentOrElse(cachedAvailabilityData -> checkAndProcessCachedAvailabilityData(
                                seatAvailabilityResponseDto, cache, cachedAvailabilityData),
                        () -> setCache(seatAvailabilityResponseDto, cache)
                );
    }

    private void setCache(SeatAvailabilityResponseDto seatAvailabilityResponseDto, Cache cache) {
        Optional.of(seatAvailabilityResponseDto)
                .map(SeatAvailabilityResponseDto::getAvlDayList)
                .filter(Predicate.not(List::isEmpty))
                .ifPresent(availabilityDayDtos -> {
                    log.debug("Availability data not found in cache:::" +
                                    "Setting cache and sending email {}",
                            seatAvailabilityResponseDto);
                    Optional.ofNullable(cache)
                            .ifPresent(cache1 -> cache1
                                    .put(getCacheKey(seatAvailabilityResponseDto
                                                    .getSeatAvailabilityRequestDto()),
                                            seatAvailabilityResponseDto));
                    setMailParams(seatAvailabilityResponseDto
                                    .getSeatAvailabilityRequestDto(),
                            seatAvailabilityResponseDto, Boolean.FALSE);
                });
    }

    private void checkAndProcessCachedAvailabilityData(SeatAvailabilityResponseDto seatAvailabilityResponseDto,
                                                       Cache cache, List<AvailabilityDayDto> cachedAvailabilityData) {
        Optional.of(seatAvailabilityResponseDto)
                .map(SeatAvailabilityResponseDto::getAvlDayList)
                .filter(Predicate.not(List::isEmpty))
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
                });
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
                                .append(seatAvailabilityRequestDto1.getToStnCode())
                                .append(seatAvailabilityRequestDto1.getEmailDtoList()
                                        .stream()
                                        .map(EmailDto::getEmailAddress)
                                        .collect(Collectors.joining()))
                                .toString())
                .orElseThrow(() -> new IllegalArgumentException("Invalid seatAvailabilityRequestDto"));
    }

    private void setMailParams(SeatAvailabilityRequestDto seatAvailabilityRequestDto,
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
                .append(" to ").append(seatAvailabilityRequestDto.getToDate())
                .append(" from ").append(seatAvailabilityRequestDto.getFromStnCode())
                .append(" to ").append(seatAvailabilityRequestDto.getToStnCode())
                .append(" in class ")
                .append(seatAvailabilityRequestDto.getClassCode().getName())
                .append(" quota ").append(seatAvailabilityRequestDto.getQuotaCode().name())
                .toString());
    }
}
