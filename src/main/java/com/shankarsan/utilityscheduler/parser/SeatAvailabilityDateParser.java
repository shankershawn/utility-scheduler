package com.shankarsan.utilityscheduler.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class SeatAvailabilityDateParser {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public Date parse(String dateString) {
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException pe) {
            log.error("Exception while parsing date", pe);
        }
        return date;
    }

    public String format(Date date) {
        return simpleDateFormat.format(date);
    }

}
