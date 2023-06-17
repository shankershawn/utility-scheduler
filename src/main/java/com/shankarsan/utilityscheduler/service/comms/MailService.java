package com.shankarsan.utilityscheduler.service.comms;

import com.shankarsan.utilityscheduler.dto.EmailDto;

import java.io.File;
import java.util.List;

public interface MailService {

    void sendMail(List<EmailDto> recipients, String body, String subject, List<File> attachments);
}
