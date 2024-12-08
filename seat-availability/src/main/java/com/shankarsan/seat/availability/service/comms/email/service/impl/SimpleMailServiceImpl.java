package com.shankarsan.seat.availability.service.comms.email.service.impl;

import com.shankarsan.seat.availability.configuration.ApplicationConfiguration;
import com.shankarsan.seat.availability.constants.CommonConstants;
import com.shankarsan.seat.availability.dto.EmailDto;
import com.shankarsan.seat.availability.service.comms.email.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service("plain")
@RequiredArgsConstructor
@Slf4j
public class SimpleMailServiceImpl implements MailService {

  private final MailSender mailSender;

  private final List<String> subjectList = new ArrayList<>();

  private final ApplicationConfiguration applicationConfiguration;

  @Override
  public void sendMail(List<EmailDto> recipients, String body, String subject, @Nullable List<File> attachments) {
    if (Boolean.FALSE.equals(applicationConfiguration.getMailFlag())) {
      log.debug("Skipping send email");
      return;
    }
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    Map<EmailDto.EmailAddressLevel, List<EmailDto>> map = recipients.stream()
        .collect(Collectors.groupingBy(EmailDto::getEmailAddressLevel));
    Optional.ofNullable(map.get(EmailDto.EmailAddressLevel.TO))
        .map(Collection::stream)
        .map(e -> e.map(EmailDto::getEmailAddress)
            .toArray(String[]::new))
        .ifPresent(simpleMailMessage::setTo);
    Optional.ofNullable(map.get(EmailDto.EmailAddressLevel.CC))
        .map(Collection::stream)
        .map(e -> e.map(EmailDto::getEmailAddress)
            .toArray(String[]::new))
        .ifPresent(simpleMailMessage::setCc);
    Optional.ofNullable(map.get(EmailDto.EmailAddressLevel.BCC))
        .map(Collection::stream)
        .map(e -> e.map(EmailDto::getEmailAddress)
            .toArray(String[]::new))
        .ifPresent(simpleMailMessage::setBcc);
    if (subjectList.contains(subject)) {
      subject = CommonConstants.EMAIL_REPLY_PREFIX + subject;
    } else {
      subjectList.add(subject);
    }
    simpleMailMessage.setSubject(subject);
    simpleMailMessage.setReplyTo(CommonConstants.EMAIL_NOREPLY_UTILITY_SCHEDULER_COM);
    simpleMailMessage.setText(body);
    mailSender.send(simpleMailMessage);
  }
}
