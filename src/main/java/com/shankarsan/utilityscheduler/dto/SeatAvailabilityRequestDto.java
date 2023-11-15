package com.shankarsan.utilityscheduler.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class SeatAvailabilityRequestDto implements Serializable {

    private static final long serialVersionUID = -1L;

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
    private String fromDate; //DD-MM-YYYY format
    private String toDate; //DD-MM-YYYY format
    private ClassCode classCode;
    private List<EmailDto> emailDtoList;
    private List<Integer> runDays;

    public enum QuotaCode {
        GN, TQ, LD, SS
    }

    @Getter
    public enum ClassCode {
        _2S, _CC, _EC, _1A, _2A, _3A, _3E, _SL;

        public String getName() {
            String name = name();
            if (name.charAt(0) == '_') {
                name = name.substring(1);
            }
            return name;
        }


    }
}
