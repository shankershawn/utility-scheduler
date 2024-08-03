package com.shankarsan.utilityscheduler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityDayDto implements Serializable, Comparable<AvailabilityDayDto> {

    private static final long serialVersionUID = -3L;

    @JsonProperty("availablityDate")
    private String availabilityDate; //18-7-2023
    @JsonProperty("availablityStatus")
    private String availabilityStatus; //RLWL1/WL1
    private Integer availabilityStatusRank;
    private transient String reasonType; //S
    private transient Integer availabilityChange;
    @JsonProperty("availablityType")
    private transient String availabilityType; //3
    private transient String currentBkgFlag; //N
    private transient String wlType; //12

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AvailabilityDayDto that = (AvailabilityDayDto) o;
        return this.availabilityDate.equals(that.availabilityDate)
                && this.availabilityStatus.equals(that.availabilityStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(availabilityDate, availabilityStatus);
    }

    public void setAvailabilityChange(AvailabilityDayDto o) {
        this.availabilityChange = compareTo(o);
    }

    @Override
    public int compareTo(@NonNull AvailabilityDayDto o) {

        if (Optional.ofNullable(this.availabilityStatusRank).isPresent()
                && Optional.ofNullable(o.availabilityStatusRank).isPresent()) {
            if (Objects.equals(this.availabilityStatusRank, o.availabilityStatusRank)) {
                if ("1".equals(this.availabilityType)) {
                    return this.availabilityStatus.compareTo(o.availabilityStatus);
                } else {
                    return o.availabilityStatus.compareTo(this.availabilityStatus);
                }
            } else {
                return o.availabilityStatusRank - this.availabilityStatusRank;
            }
        }
        return 0;
    }
}
