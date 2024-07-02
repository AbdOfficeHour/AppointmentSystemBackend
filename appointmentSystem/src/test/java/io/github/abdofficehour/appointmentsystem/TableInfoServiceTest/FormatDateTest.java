package io.github.abdofficehour.appointmentsystem.TableInfoServiceTest;

import io.github.abdofficehour.appointmentsystem.pojo.OfficeHourEvent;
import io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable.OfficeHourTime;
import io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable.TimeTable;
import io.github.abdofficehour.appointmentsystem.service.TableInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试时间表格式化
 */
@SpringBootTest
public class FormatDateTest {

    @Autowired
    TableInfoService tableInfoService;

    @Test
    void testFormatDate(){

        List<OfficeHourTime> officeHourTimes = new ArrayList<>(){{
           add(new OfficeHourTime(
                   LocalDate.of(2024,3,10).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                   LocalDateTime.of(2024,3,10,14,0).toInstant(ZoneOffset.UTC).toEpochMilli(),
                   LocalDateTime.of(2024,3,10,17,0).toInstant(ZoneOffset.UTC).toEpochMilli()
           ));
            add(new OfficeHourTime(
                    LocalDate.of(2024,3,11).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                    LocalDateTime.of(2024,3,10,14,0).toInstant(ZoneOffset.UTC).toEpochMilli(),
                    LocalDateTime.of(2024,3,10,17,30).toInstant(ZoneOffset.UTC).toEpochMilli()
                    ));
            add(new OfficeHourTime(
                    LocalDate.of(2024,3,12).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                    LocalDateTime.of(2024,3,10,14,0).toInstant(ZoneOffset.UTC).toEpochMilli(),
                    LocalDateTime.of(2024,3,10,17,0).toInstant(ZoneOffset.UTC).toEpochMilli()
                    ));
        }};


        List<OfficeHourEvent> officeHourEvents = new ArrayList<>(){{
           add(new OfficeHourEvent(
                LocalDate.of(2024,3,10),
                LocalDateTime.of(2024,3,10,14,30),
                LocalDateTime.of(2024,3,10,15,10)
           ));
            add(new OfficeHourEvent(
                    LocalDate.of(2024,3,10),
                    LocalDateTime.of(2024,3,10,15,40),
                    LocalDateTime.of(2024,3,10,16,10)
            ));
            add(new OfficeHourEvent(
                    LocalDate.of(2024,3,11),
                    LocalDateTime.of(2024,3,10,14,30),
                    LocalDateTime.of(2024,3,10,15,10)
            ));
        }};



        List<TimeTable> timeTables = tableInfoService.formatTimetable(officeHourTimes,officeHourEvents);

        for (TimeTable timeTable:timeTables){
            System.out.println(timeTable);
            System.out.println(timeTable.getBusy().size());
            System.out.println(timeTable.getAvailable().size());
        }

    }

}
