package com.shankarsan.utilityscheduler.service.comms.impl;

import com.shankarsan.utilityscheduler.configuration.ApplicationConfiguration;
import com.shankarsan.utilityscheduler.dto.EmailDto;
import com.shankarsan.utilityscheduler.exception.ApplicationException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HtmlMailServiceImplTest {

    @Mock
    private JavaMailSenderImpl javaMailSender;

    @Mock
    private ApplicationConfiguration applicationConfiguration;

    @InjectMocks
    private HtmlMailServiceImpl htmlMailService;

    private static List<EmailDto> recipients;

    private static String body;

    private static String subject;

    private static List<File> attachments;

    @BeforeAll
    static void setup() {
        recipients = List.of(
                EmailDto.builder().emailAddress("abc@def.com")
                        .emailAddressLevel(EmailDto.EmailAddressLevel.TO).build(),
                EmailDto.builder().emailAddress("mno@pqr.com")
                        .emailAddressLevel(EmailDto.EmailAddressLevel.BCC).build()
        );
        body = "Test body";
        subject = "Test subject";
        attachments = null;
    }

    @Test
    void shouldReturnWhenMailFlagIsFalse() {
        lenient().when(applicationConfiguration.getMailFlag()).thenReturn(Boolean.FALSE);
        htmlMailService.sendMail(recipients, body, subject, attachments);
        verify(javaMailSender, times(0)).send(any(MimeMessage.class));
    }

    @Test
    void shouldSendMail() {
        when(applicationConfiguration.getMailFlag()).thenReturn(Boolean.TRUE);
        when(javaMailSender.getJavaMailProperties()).thenReturn(new Properties());
        htmlMailService.sendMail(recipients, body, subject, attachments);
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void shouldThrowApplicationException() {
        when(applicationConfiguration.getMailFlag()).thenThrow(ApplicationException.class);
        assertThrows(ApplicationException.class,
                () -> htmlMailService.sendMail(recipients, body, subject, attachments));
        verify(javaMailSender, times(0)).send(any(MimeMessage.class));
    }
}
