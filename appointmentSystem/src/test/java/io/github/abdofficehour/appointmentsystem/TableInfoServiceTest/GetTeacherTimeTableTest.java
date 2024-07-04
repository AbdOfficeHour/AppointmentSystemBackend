package io.github.abdofficehour.appointmentsystem.TableInfoServiceTest;

import io.github.abdofficehour.appointmentsystem.mapper.OfficeHourEventMapper;
import io.github.abdofficehour.appointmentsystem.mapper.TeacherTimeTableMapper;
import io.github.abdofficehour.appointmentsystem.pojo.data.OfficeHourEvent;
import io.github.abdofficehour.appointmentsystem.pojo.data.TeacherTimeTable;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.OfficeHourTimetable;
import io.github.abdofficehour.appointmentsystem.service.TableInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 测试获取教师时间表
 */
@SpringBootTest
public class GetTeacherTimeTableTest {

    @Autowired
    private TeacherTimeTableMapper teacherTimeTableMapper;

    @Autowired
    private OfficeHourEventMapper officeHourEventMapper;

    @Autowired
    private TableInfoService tableInfoService;

    @Test
    @Transactional
    public void testGetTeacherTimeTable(){
        // 插入一下测试用例
        teacherTimeTableMapper.insertBatch(new ArrayList<>(){{
            add(new TeacherTimeTable(
                    0,
                    LocalDate.of(2024,7,10),
                    LocalDateTime.of(2024,7,10,14,0),
                    LocalDateTime.of(2024,7,10,17,0),
                    "scun001"
            ));
            add(new TeacherTimeTable(
                    0,
                    LocalDate.of(2024,7,11),
                    LocalDateTime.of(2024,7,10,14,0),
                    LocalDateTime.of(2024,7,10,17,30),
                    "scun001"
            ));
            add(new TeacherTimeTable(
                    0,
                    LocalDate.of(2024,7,12),
                    LocalDateTime.of(2024,7,10,14,0),
                    LocalDateTime.of(2024,7,10,17,0),
                    "scun001"
            ));
        }});

        officeHourEventMapper.insertBatch(new ArrayList<>(){{
            add(new OfficeHourEvent(
                    LocalDate.of(2024,7,10),
                    LocalDateTime.of(2024,7,10,14,30),
                    LocalDateTime.of(2024,7,10,15,10),
                    "20223803053",
                    "scun001"
            ));
            add(new OfficeHourEvent(
                    LocalDate.of(2024,7,10),
                    LocalDateTime.of(2024,7,10,15,40),
                    LocalDateTime.of(2024,7,10,16,10),
                    "20223803053",
                    "scun001"
            ));
            add(new OfficeHourEvent(
                    LocalDate.of(2024,7,11),
                    LocalDateTime.of(2024,7,10,14,30),
                    LocalDateTime.of(2024,7,10,15,10),
                    "20223803053",
                    "scun001"
            ));
        }});

        OfficeHourTimetable officeHourTimetable = tableInfoService.getTeacherTimeTable("scun001");
        System.out.print(officeHourTimetable);
    }

}
