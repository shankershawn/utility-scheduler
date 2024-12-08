package com.shankarsan.seat.availability.consumer;

import com.shankarsan.seat.availability.configuration.ApplicationConfiguration;
import com.shankarsan.seat.availability.dto.AvailabilityDayDto;
import com.shankarsan.seat.availability.dto.EmailDto;
import com.shankarsan.seat.availability.dto.SeatAvailabilityRequestDto;
import com.shankarsan.seat.availability.dto.SeatAvailabilityResponseDto;
import com.shankarsan.seat.availability.service.comms.email.service.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatAvailabilityEmailProcessorTest {

  @Mock
  private CacheManager cacheManager;

  @Mock
  private MailService mailService;

  @Mock
  private ApplicationConfiguration applicationConfiguration;

  @InjectMocks
  private SeatAvailabilityEmailProcessor seatAvailabilityEmailProcessor;

  @Mock
  private Cache cache;

  @Captor
  private ArgumentCaptor<List<EmailDto>> emailDtoListArgumentCaptor;

  @Captor
  private ArgumentCaptor<String> bodyArgumentCaptor;

  @Captor
  private ArgumentCaptor<String> subjectArgumentCaptor;

  @Captor
  private ArgumentCaptor<List<File>> attachmentsArgumentCaptor;

  @ParameterizedTest
  @MethodSource("getChangedAvailabilityStatus")
  void shouldSendMailForChangedAvailabilityStatusPresentInCache(
      String afterAvailabilityStatus, SeatAvailabilityResponseDto cachedSeatAvailabilityResponseDto) {

    SeatAvailabilityResponseDto seatAvailabilityResponseDto =
        getSeatAvailabilityResponseDto(afterAvailabilityStatus);

    when(cacheManager.getCache(anyString())).thenReturn(cache);
    when(cache.get(any())).thenReturn(new SimpleValueWrapper(cachedSeatAvailabilityResponseDto));

    Stream.of(seatAvailabilityResponseDto).forEach(seatAvailabilityEmailProcessor);
    verify(mailService, times(1)).sendMail(emailDtoListArgumentCaptor.capture(),
        bodyArgumentCaptor.capture(), subjectArgumentCaptor.capture(), attachmentsArgumentCaptor.capture());

    assertEquals(seatAvailabilityResponseDto.getEmailDtoList(), emailDtoListArgumentCaptor.getValue());
    assertNotNull(emailDtoListArgumentCaptor.getValue());
    assertNotEquals(-1, bodyArgumentCaptor.getValue()
        .indexOf(seatAvailabilityResponseDto.getAvlDayList().get(0).getAvailabilityStatus()));
    assertNotNull(subjectArgumentCaptor.getValue());
    assertEquals(seatAvailabilityResponseDto.getMailSubject(), subjectArgumentCaptor.getValue());
    assertNotNull(seatAvailabilityResponseDto.getSeatAvailabilityRequestDto());
    assertNull(attachmentsArgumentCaptor.getValue());
  }

  @ParameterizedTest
  @MethodSource("getUnchangedAvailabilityStatus")
  void shouldNotSendMailForUnchangedAvailabilityStatusPresentInCache(String beforeAvailabilityStatus,
                                                                     String afterAvailabilityStatus) {
    SeatAvailabilityResponseDto cachedSeatAvailabilityResponseDto =
        getSeatAvailabilityResponseDto(beforeAvailabilityStatus);

    SeatAvailabilityResponseDto seatAvailabilityResponseDto =
        getSeatAvailabilityResponseDto(afterAvailabilityStatus);

    when(cacheManager.getCache(anyString())).thenReturn(cache);
    when(cache.get(any())).thenReturn(new SimpleValueWrapper(cachedSeatAvailabilityResponseDto));

    Stream.of(seatAvailabilityResponseDto).forEach(seatAvailabilityEmailProcessor);
    verify(mailService, times(0)).sendMail(any(), any(), any(), any());
  }

  @Test
  void shouldNotSendMailForNotAllowedErrorMessageInSeatAvailabilityResponseDto() {
    SeatAvailabilityResponseDto seatAvailabilityResponseDto =
        getSeatAvailabilityResponseDto();
    seatAvailabilityResponseDto.setErrorMessage("8000 Some error");

    when(applicationConfiguration.getAllowedErrorCodes()).thenReturn(List.of("8001"));

    Stream.of(seatAvailabilityResponseDto).forEach(seatAvailabilityEmailProcessor);

    verify(cacheManager, times(0)).getCache(anyString());
    verify(mailService, times(0)).sendMail(any(), any(), any(), any());
  }

  @Test
  void shouldSendMailForAllowedErrorMessageInSeatAvailabilityResponseDto() {
    SeatAvailabilityResponseDto seatAvailabilityResponseDto =
        getSeatAvailabilityResponseDto("GNWL34/WL14");
    seatAvailabilityResponseDto.setErrorMessage("8001 Some error");

    when(applicationConfiguration.getAllowedErrorCodes()).thenReturn(List.of("8001"));

    Stream.of(seatAvailabilityResponseDto).forEach(seatAvailabilityEmailProcessor);

    verify(cacheManager, times(1)).getCache(anyString());
    verify(mailService, times(1)).sendMail(any(), any(), any(), any());
  }

  private static SeatAvailabilityResponseDto getSeatAvailabilityResponseDto() {
    SeatAvailabilityRequestDto seatAvailabilityRequestDto = getSeatAvailabilityRequestDto();
    return SeatAvailabilityResponseDto.builder()
        .seatAvailabilityRequestDto(seatAvailabilityRequestDto)
        .errorMessage(null)
        .trainName("Rajdhani Express")
        .mailSubject("Test Subject")
        .avlDayList(getAvailabilityDayList())
        .build();
  }

  private static Stream<Arguments> getChangedAvailabilityStatus() {
    return Stream.of(Arguments.of("AVAILABLE-99",
            getSeatAvailabilityResponseDto("AVAILABLE-100")),
        Arguments.of("RAC 5/RAC 5",
            getSeatAvailabilityResponseDto("RAC 4/RAC 4")),
        Arguments.of("GNWL34/WL14",
            getSeatAvailabilityResponseDto("GNWL34/WL15")),
        Arguments.of("REGRET",
            getSeatAvailabilityResponseDto("GNWL38/WL18")),
        Arguments.of("AVAILABLE-98", null),
        Arguments.of("RAC 5/RAC 3", null),
        Arguments.of("GNWL34/WL10", null),
        Arguments.of("TRAIN DEPARTED",
            getSeatAvailabilityResponseDto("GNWL34/WL14")),
        Arguments.of("INVALID",
            getSeatAvailabilityResponseDto("GNWL34/WL14")));
  }

  private static Stream<Arguments> getUnchangedAvailabilityStatus() {
    return Stream.of(Arguments.of("AVAILABLE-100", "AVAILABLE-100"),
        Arguments.of("RAC 4/RAC 4", "RAC 4/RAC 4"),
        Arguments.of("GNWL34/WL15", "GNWL34/WL15"));
  }

  private static SeatAvailabilityResponseDto getSeatAvailabilityResponseDto(String availabilityStatus) {
    SeatAvailabilityResponseDto seatAvailabilityResponseDto = getSeatAvailabilityResponseDto();
    Optional
        .of(seatAvailabilityResponseDto)
        .map(SeatAvailabilityResponseDto::getAvlDayList)
        .ifPresent(availabilityDayDtos -> availabilityDayDtos
            .forEach(availabilityDayDto -> availabilityDayDto
                .setAvailabilityStatus(availabilityStatus)));
    return seatAvailabilityResponseDto;
  }

  private static List<AvailabilityDayDto> getAvailabilityDayList() {
    return List.of(AvailabilityDayDto.builder()
            .availabilityDate("16-12-2023")
            .availabilityType("AVAILABILITY_TYPE")
            .availabilityChange(0)
            .currentBkgFlag("N")
            .reasonType("S")
            .wlType("GNWL")
            .build(),
        AvailabilityDayDto.builder()
            .availabilityDate("17-12-2023")
            .availabilityType("AVAILABILITY_TYPE")
            .availabilityChange(1)
            .currentBkgFlag("N")
            .reasonType("S")
            .wlType("GNWL")
            .build(),
        AvailabilityDayDto.builder()
            .availabilityDate("18-12-2023")
            .availabilityType("AVAILABILITY_TYPE")
            .availabilityChange(-1)
            .currentBkgFlag("N")
            .reasonType("S")
            .wlType("GNWL")
            .build());
  }

  private static SeatAvailabilityRequestDto getSeatAvailabilityRequestDto() {
    return SeatAvailabilityRequestDto.builder()
        .runDays(List.of(1, 2, 3, 4, 5, 6, 7))
        .toDate("16-12-2023")
        .fromDate("16-12-2023")
        .emailDtoList(List.of(EmailDto.builder()
            .emailAddressLevel(EmailDto.EmailAddressLevel.TO)
            .emailAddress("shankershawn@gmail.com")
            .build()))
        .trainNumber("12301")
        .quotaCode(SeatAvailabilityRequestDto.QuotaCode.GN)
        .journeyDate("16-12-2023")
        .toStnCode("HWH")
        .fromStnCode("NDLS")
        .classCode(SeatAvailabilityRequestDto.ClassCode._3A)
        .build();
  }

}
