package com.shankarsan.utilityscheduler.dto;

import lombok.Builder;

@Builder
public class EmailDto {

    private String emailAddress;

    private EmailAddressLevel emailAddressLevel;

    enum EmailAddressLevel {
        TO,CC,BCC
    }
}
