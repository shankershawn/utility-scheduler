package com.shankarsan.seat.availability.service.comms.email.service;

import com.shankarsan.seat.availability.dto.EmailDto;
import org.springframework.lang.Nullable;

import java.io.File;
import java.util.List;

public interface MailService {

    void sendMail(List<EmailDto> recipients, String body, String subject, @Nullable List<File> attachments);
}
