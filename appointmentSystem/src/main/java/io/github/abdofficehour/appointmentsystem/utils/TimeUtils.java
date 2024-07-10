package io.github.abdofficehour.appointmentsystem.utils;

import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
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

    public Long toTimeStamp(Date date){
        return date.toLocalDate().atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
    }

    public LocalDateTime DateTimeFromTimeStamp(Long timeStamp){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),ZoneOffset.systemDefault());
    }

    public LocalDate DateFromTimeStamp(Long timeStamp){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),ZoneOffset.systemDefault()).toLocalDate();
    }
}
