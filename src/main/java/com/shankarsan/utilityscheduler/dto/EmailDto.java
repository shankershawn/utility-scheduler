package com.shankarsan.utilityscheduler.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class EmailDto implements Serializable {

    private String emailAddress;

    @Builder.Default
    private EmailAddressLevel emailAddressLevel = EmailAddressLevel.TO;

    public enum EmailAddressLevel implements Serializable {
        TO, CC, BCC
    }
}
