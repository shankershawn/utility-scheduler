package com.shankarsan.utilityscheduler.service.comms.impl;

import com.shankarsan.utilityscheduler.configuration.ApplicationConfiguration;
import com.shankarsan.utilityscheduler.dto.EmailDto;
import com.shankarsan.utilityscheduler.exception.ApplicationException;
import com.shankarsan.utilityscheduler.service.comms.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service("html")
@RequiredArgsConstructor
@Slf4j
public class HtmlMailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    private final ApplicationConfiguration applicationConfiguration;

    @Override
    public void sendMail(List<EmailDto> recipients, String body, String subject, List<File> attachments) {
        if (Boolean.FALSE.equals(applicationConfiguration.getMailFlag())) {
            log.debug("Skipping send email");
            return;
        }
        Session session = Session.getInstance(((JavaMailSenderImpl) javaMailSender).getJavaMailProperties());
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setContent(body, "text/html");
            MimeMailMessage mimeMailMessage = new MimeMailMessage(mimeMessage);
            mimeMailMessage.setSubject(subject);
            Optional.ofNullable(recipients)
                    .map(Collection::stream)
                    .map(emailDtoStream -> emailDtoStream
                            .filter(emailDto -> EmailDto.EmailAddressLevel.TO.equals(emailDto.getEmailAddressLevel()))
                            .map(EmailDto::getEmailAddress)
                            .toArray(String[]::new)
                    )
                    .ifPresent(mimeMailMessage::setTo);
            Optional.ofNullable(recipients)
                    .map(Collection::stream)
                    .map(emailDtoStream -> emailDtoStream
                            .filter(emailDto -> EmailDto.EmailAddressLevel.CC.equals(emailDto.getEmailAddressLevel()))
                            .map(EmailDto::getEmailAddress)
                            .toArray(String[]::new)
                    ).ifPresent(mimeMailMessage::setCc);
            Optional.ofNullable(recipients)
                    .map(Collection::stream)
                    .map(emailDtoStream -> emailDtoStream
                            .filter(emailDto -> EmailDto.EmailAddressLevel.BCC.equals(emailDto.getEmailAddressLevel()))
                            .map(EmailDto::getEmailAddress)
                            .toArray(String[]::new)
                    ).ifPresent(mimeMailMessage::setBcc);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new ApplicationException(e);
        }
    }
}
