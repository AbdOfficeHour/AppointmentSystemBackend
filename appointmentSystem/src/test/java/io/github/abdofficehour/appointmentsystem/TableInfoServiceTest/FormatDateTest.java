package io.github.abdofficehour.appointmentsystem.TableInfoServiceTest;

import io.github.abdofficehour.appointmentsystem.config.Properties;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.TableEvent;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.TimeTable;
import io.github.abdofficehour.appointmentsystem.service.TableInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试时间表格式化
 */
@SpringBootTest
public class FormatDateTest {

    @Autowired
    TableInfoService tableInfoService;

    @Autowired
    Properties properties;

    @Test
    void testFormatDate(){
        List<TableEvent> tableEvents = new ArrayList<>(){{
           add(new TableEvent(
                LocalDate.of(2024,7,4),
                LocalDateTime.of(2024,7,4,14,30),
                LocalDateTime.of(2024,7,4,15,10),
                   2
           ));
            add(new TableEvent(
                    LocalDate.of(2024,7,4),
                    LocalDateTime.of(2024,7,4,15,40),
                    LocalDateTime.of(2024,7,4,16,10),
                    2
            ));
            add(new TableEvent(
                    LocalDate.of(2024,7,6),
                    LocalDateTime.of(2024,7,6,14,30),
                    LocalDateTime.of(2024,7,6,15,10),
                    2
            ));
        }};

        LocalDate today = LocalDate.now();
        LocalDate endDay = today.plusDays(properties.getDateLen());

        List<TimeTable> timeTables = tableInfoService.formatTimetable(today,endDay,tableEvents);

        for (TimeTable timeTable:timeTables){
            System.out.println(timeTable);
            System.out.println(timeTable.getBusy().size());
            System.out.println(timeTable.getAvailable().size());
        }

    }

}
