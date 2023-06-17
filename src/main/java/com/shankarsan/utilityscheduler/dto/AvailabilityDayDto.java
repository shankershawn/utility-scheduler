package com.shankarsan.utilityscheduler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AvailabilityDayDto {
    @JsonProperty("availablityDate")
    private String availabilityDate; //18-7-2023
    @JsonProperty("availablityStatus")
    private String availabilityStatus; //RLWL1/WL1
    private String reasonType; //S
    @JsonProperty("availablityType")
    private String availabilityType; //3
    private String currentBkgFlag; //N
    private String wlType; //12
}
