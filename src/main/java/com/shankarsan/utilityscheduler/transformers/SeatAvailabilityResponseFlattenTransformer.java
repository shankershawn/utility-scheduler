package com.shankarsan.utilityscheduler.transformers;

import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class SeatAvailabilityResponseFlattenTransformer implements
        Function<List<SeatAvailabilityResponseDto>, SeatAvailabilityResponseDto> {
    @Override
    public SeatAvailabilityResponseDto apply(List<SeatAvailabilityResponseDto> seatAvailabilityResponseDtos) {
        return SeatAvailabilityResponseDto.builder()
                .seatAvailabilityRequestDto(getOptionalWithNewStream(seatAvailabilityResponseDtos)
                        .map(seatAvailabilityResponseDtoStream ->
                                seatAvailabilityResponseDtoStream
                                        .findAny()
                                        .map(SeatAvailabilityResponseDto::getSeatAvailabilityRequestDto)
                                        .orElseThrow(() -> new IllegalStateException("Request not set")))
                        .orElse(null))
                .errorMessage(getOptionalWithNewStream(seatAvailabilityResponseDtos)
                        .map(seatAvailabilityResponseDtoStream ->
                                seatAvailabilityResponseDtoStream
                                        .findAny()
                                        .map(SeatAvailabilityResponseDto::getErrorMessage)
                                        .orElse(null))
                        .orElse(null))
                .trainName(getOptionalWithNewStream(seatAvailabilityResponseDtos)
                        .map(seatAvailabilityResponseDtoStream ->
                                seatAvailabilityResponseDtoStream
                                        .findAny()
                                        .map(SeatAvailabilityResponseDto::getTrainName)
                                        .orElse(null))
                        .orElse(null))
                .avlDayList(getOptionalWithNewStream(seatAvailabilityResponseDtos)
                        .map(seatAvailabilityResponseDtoStream ->
                                seatAvailabilityResponseDtoStream
                                        .flatMap(seatAvailabilityResponseDto -> Optional
                                                .ofNullable(seatAvailabilityResponseDto)
                                                .map(SeatAvailabilityResponseDto::getAvlDayList)
                                                .map(Collection::stream)
                                                .orElse(Stream.empty()))
                                        .collect(Collectors.toList())
                        ).orElse(null))
                .emailDtoList(getOptionalWithNewStream(seatAvailabilityResponseDtos)
                        .map(seatAvailabilityResponseDtoStream ->
                                seatAvailabilityResponseDtoStream
                                        .findAny()
                                        .map(SeatAvailabilityResponseDto::getEmailDtoList)
                                        .orElse(null)
                        ).orElse(null))
                .mailSubject(getOptionalWithNewStream(seatAvailabilityResponseDtos)
                        .map(seatAvailabilityResponseDtoStream ->
                                seatAvailabilityResponseDtoStream
                                        .findAny()
                                        .map(SeatAvailabilityResponseDto::getMailSubject)
                                        .orElse(null))
                        .orElse(null))
                .build();
    }

    private Optional<Stream<SeatAvailabilityResponseDto>> getOptionalWithNewStream
            (List<SeatAvailabilityResponseDto> seatAvailabilityResponseDtos) {
        return Optional
                .ofNullable(seatAvailabilityResponseDtos)
                .map(Collection::stream);
    }
}
