package com.shankarsan.utilityscheduler.parser;

import com.shankarsan.utilityscheduler.exception.ApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SeatAvailabilityDateParserTest {

    @InjectMocks
    private SeatAvailabilityDateParser seatAvailabilityDateParser;

    @Test
    void shouldParseDate() throws ParseException {
        Date date = seatAvailabilityDateParser.parse("01-02-2024");
        String dateString = seatAvailabilityDateParser.format(date);
        assertNotNull(date);
        assertEquals(new SimpleDateFormat("dd-MM-yyyy").parse("01-02-2024"), date);
        assertEquals("01-02-2024", dateString);
    }

    @Test
    void shouldThrowApplicationException() {
        assertThrows(ApplicationException.class, () -> seatAvailabilityDateParser.parse("23234"));
    }

}
