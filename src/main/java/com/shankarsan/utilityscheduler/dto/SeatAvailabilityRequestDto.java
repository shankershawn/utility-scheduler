package com.shankarsan.utilityscheduler.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class SeatAvailabilityRequestDto {
    private final String paymentFlag = "N";
    private final Boolean concessionBooking = false;
    private final Boolean ftBooking = false;
    private final Boolean loyaltyRedemptionBooking = false;
    private final String ticketType = "E";
    private QuotaCode quotaCode;
    private final Boolean moreThanOneDay = true;
    private final Boolean returnJourney = false;
    private final Boolean returnTicket = false;
    private String trainNumber;
    private String fromStnCode;
    private String toStnCode;
    private final Boolean isLogedinReq = false;
    private String journeyDate; //YYYYMMDD format
    private ClassCode classCode;

    public enum QuotaCode {
        GN;
    }

    @Getter
    public enum ClassCode {
        _2S;

        public ClassCode getName() {
            String name = name();
            if(name.charAt(0) == '_') {
                name = name.substring(1, name.length());
            }
            return Enum.valueOf(ClassCode.class, name);
        }


    }
}
