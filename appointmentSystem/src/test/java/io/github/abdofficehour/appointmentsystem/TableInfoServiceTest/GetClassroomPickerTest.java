package io.github.abdofficehour.appointmentsystem.TableInfoServiceTest;

import io.github.abdofficehour.appointmentsystem.service.TableInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GetClassroomPickerTest {

    @Autowired
    TableInfoService tableInfoService;

    @Test
    public void testGetClassroomPicker(){
        System.out.println(tableInfoService.getClassroomPicker());
    }

}
