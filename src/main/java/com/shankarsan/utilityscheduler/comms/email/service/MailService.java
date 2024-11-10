package com.shankarsan.utilityscheduler.comms.email.service;

import com.shankarsan.utilityscheduler.dto.EmailDto;
import org.springframework.lang.Nullable;

import java.io.File;
import java.util.List;

public interface MailService {

    void sendMail(List<EmailDto> recipients, String body, String subject, @Nullable List<File> attachments);
}
