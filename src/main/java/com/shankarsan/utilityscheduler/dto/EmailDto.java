package com.shankarsan.utilityscheduler.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmailDto {

    private String emailAddress;

    @Builder.Default
    private EmailAddressLevel emailAddressLevel = EmailAddressLevel.TO;

    public enum EmailAddressLevel {
        TO, CC, BCC
    }
}
