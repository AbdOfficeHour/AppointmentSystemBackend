package io.github.abdofficehour.appointmentsystem.TableInfoServiceTest;

import io.github.abdofficehour.appointmentsystem.mapper.ClassroomEventMapper;
import io.github.abdofficehour.appointmentsystem.pojo.data.ClassroomEvent;
import io.github.abdofficehour.appointmentsystem.pojo.enumclass.Aim;
import io.github.abdofficehour.appointmentsystem.service.TableInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@SpringBootTest
public class GetClassroomTimeTableTest {

    @Autowired
    private ClassroomEventMapper classroomEventMapper;

    @Autowired
    private TableInfoService tableInfoService;

    @Test
    @Transactional
    public void testGetClassroomTimeTable(){

        classroomEventMapper.insertClassroomEventBatch(new ArrayList<>(){{
            add(new ClassroomEvent(
                    LocalDate.of(2024,7,5),
                    LocalDateTime.of(2024,7,5,14,0),
                    LocalDateTime.of(2024,7,5,14,30),
                    "20223803053",
                    1,
                    false,
                    false,
                    false,
                    Aim.DISCUSS,
                    "",
                    "",
                    1
            ));
            add(new ClassroomEvent(
                    LocalDate.of(2024,7,5),
                    LocalDateTime.of(2024,7,5,15,0),
                    LocalDateTime.of(2024,7,5,15,30),
                    "20223803053",
                    1,
                    false,
                    false,
                    false,
                    Aim.DISCUSS,
                    "",
                    "",
                    1
            ));
            add(new ClassroomEvent(
                    LocalDate.of(2024,7,6),
                    LocalDateTime.of(2024,7,6,14,0),
                    LocalDateTime.of(2024,7,6,14,30),
                    "20223803053",
                    1,
                    false,
                    false,
                    false,
                    Aim.DISCUSS,
                    "",
                    "",
                    1
            ));
        }});

        System.out.println(tableInfoService.getClassroomTimeTable(1));
    }


}
