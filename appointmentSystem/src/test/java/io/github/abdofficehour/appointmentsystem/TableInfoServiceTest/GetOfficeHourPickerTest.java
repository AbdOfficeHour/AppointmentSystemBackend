package io.github.abdofficehour.appointmentsystem.TableInfoServiceTest;

import io.github.abdofficehour.appointmentsystem.mapper.TeacherTimeTableMapper;
import io.github.abdofficehour.appointmentsystem.pojo.data.TeacherTimeTable;
import io.github.abdofficehour.appointmentsystem.service.TableInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 测试类型选择器
 */
@SpringBootTest
public class GetOfficeHourPickerTest {

    @Autowired
    TableInfoService tableInfoService;

    @Autowired
    TeacherTimeTableMapper teacherTimeTableMapper;

    @Test
    @Transactional
    void testGetOfficeHourPicker(){
        teacherTimeTableMapper.insertBatch(new ArrayList<>(){{
            add(new TeacherTimeTable(
                    LocalDate.of(2024,7,9),
                    LocalDateTime.of(2024,7,9,14,0),
                    LocalDateTime.of(2024,7,9,14,30),
                    "scun001"
            ));
        }});
        teacherTimeTableMapper.insertBatch(new ArrayList<>(){{
            add(new TeacherTimeTable(
                    LocalDate.of(2024,7,10),
                    LocalDateTime.of(2024,7,10,14,0),
                    LocalDateTime.of(2024,7,10,14,30),
                    "scun001"
            ));
        }});
        System.out.println(teacherTimeTableMapper.selectTeacherTimeTable(
                "scun001",
                        LocalDate.now(),
                        LocalDate.now().plusDays(14)
        ));


        System.out.println(tableInfoService.getOfficeHourPicker());
    }

}
