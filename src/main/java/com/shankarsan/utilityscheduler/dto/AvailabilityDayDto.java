package com.shankarsan.utilityscheduler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityDayDto implements Serializable {

    private static final long serialVersionUID = -3L;

    @JsonProperty("availablityDate")
    private String availabilityDate; //18-7-2023
    @JsonProperty("availablityStatus")
    private String availabilityStatus; //RLWL1/WL1
    private transient String reasonType; //S
    @JsonProperty("availablityType")
    private transient String availabilityType; //3
    private transient String currentBkgFlag; //N
    private transient String wlType; //12

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvailabilityDayDto that = (AvailabilityDayDto) o;
        return availabilityDate.equals(that.availabilityDate) && availabilityStatus.equals(that.availabilityStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(availabilityDate, availabilityStatus);
    }
}
