package com.shankarsan.utilityscheduler.service.comms;

import com.shankarsan.utilityscheduler.configuration.ApplicationConfiguration;
import com.shankarsan.utilityscheduler.dto.EmailDto;
import com.shankarsan.utilityscheduler.service.comms.impl.SimpleMailServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.io.File;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SimpleMailServiceImplTest {

    @Mock
    private MailSender javaMailSender;

    @Mock
    private ApplicationConfiguration applicationConfiguration;

    @InjectMocks
    private SimpleMailServiceImpl htmlMailService;

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
                        .emailAddressLevel(EmailDto.EmailAddressLevel.BCC).build(),
                EmailDto.builder().emailAddress("rst@uvw.com")
                        .emailAddressLevel(EmailDto.EmailAddressLevel.CC).build()
        );
        body = "Test body";
        subject = "Test subject";
        attachments = null;
    }

    @Test
    void shouldReturnWhenMailFlagIsFalse() {
        lenient().when(applicationConfiguration.getMailFlag()).thenReturn(Boolean.FALSE);
        htmlMailService.sendMail(recipients, body, subject, attachments);
        verify(javaMailSender, times(0)).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldSendMail() {
        when(applicationConfiguration.getMailFlag()).thenReturn(Boolean.TRUE);
        htmlMailService.sendMail(recipients, body, subject, attachments);
        // Second time call is for coverage
        htmlMailService.sendMail(recipients, body, subject, attachments);
        verify(javaMailSender, times(2)).send(any(SimpleMailMessage.class));
    }
}
