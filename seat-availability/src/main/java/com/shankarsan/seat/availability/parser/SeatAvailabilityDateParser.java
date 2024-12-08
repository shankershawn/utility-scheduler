package com.shankarsan.seat.availability.parser;

import com.shankarsan.seat.availability.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class SeatAvailabilityDateParser {

  private final ThreadLocal<SimpleDateFormat> simpleDateFormatThreadLocal =
      ThreadLocal.withInitial(() -> new SimpleDateFormat("dd-MM-yyyy"));

  public Date parse(String dateString) throws ApplicationException {
    Date date;
    try {
      date = simpleDateFormatThreadLocal.get().parse(dateString);
    } catch (ParseException pe) {
      log.error("ParseException while parsing date", pe);
      throw new ApplicationException(pe);
    } finally {
      simpleDateFormatThreadLocal.remove();
    }
    return date;
  }

  public String format(Date date) {
    return simpleDateFormatThreadLocal.get().format(date);
  }

}
