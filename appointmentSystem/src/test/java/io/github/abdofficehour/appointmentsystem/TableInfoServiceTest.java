package io.github.abdofficehour.appointmentsystem;

import io.github.abdofficehour.appointmentsystem.service.TableInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TableInfoServiceTest {

    @Autowired
    TableInfoService tableInfoService;

    @Test
    void testGetOfficeHourPicker(){
        System.out.println(tableInfoService.getOfficeHourPicker());
    }

}
