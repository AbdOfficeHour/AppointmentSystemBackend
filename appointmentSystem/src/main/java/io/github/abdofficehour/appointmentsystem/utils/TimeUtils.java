package io.github.abdofficehour.appointmentsystem.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class TimeUtils {

    public Long toTimeStamp(LocalDateTime dateTime){
        return dateTime.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
    }

    public Long toTimeStamp(LocalDate date){
        return date.atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
    }

}
