package com.shankarsan.seat.availability.predicate.provider;

import com.shankarsan.seat.availability.dto.AvailabilityDayDto;
import com.shankarsan.seat.availability.dto.SeatAvailabilityRequestDto;
import com.shankarsan.seat.availability.dto.SeatAvailabilityResponseDto;
import com.shankarsan.seat.availability.parser.SeatAvailabilityDateParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatAvailabilityResponseDatePredicateProvider {

  private final SeatAvailabilityDateParser seatAvailabilityDateParser;

  public Predicate<AvailabilityDayDto> getAvailabilityDayDtoPredicate(
      SeatAvailabilityResponseDto seatAvailabilityResponseDto) {
    return availabilityDayDto -> {
      Date requestToDate = Optional.ofNullable(seatAvailabilityResponseDto)
          .map(SeatAvailabilityResponseDto::getSeatAvailabilityRequestDto)
          .map(SeatAvailabilityRequestDto::getToDate)
          .map(seatAvailabilityDateParser::parse)
          .orElseThrow(() -> new IllegalStateException("To date is not available"));
      return Optional.ofNullable(availabilityDayDto)
          .map(AvailabilityDayDto::getAvailabilityDate)
          .map(seatAvailabilityDateParser::parse)
          .map(date -> !date.after(requestToDate))
          .orElseThrow(() -> new IllegalStateException("Something went wrong during date filtering"));
    };
  }
}
