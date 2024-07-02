package io.github.abdofficehour.appointmentsystem.TableInfoServiceTest;

import io.github.abdofficehour.appointmentsystem.service.TableInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 测试类型选择器
 */
@SpringBootTest
public class GetOfficeHourPickerTest {

    @Autowired
    TableInfoService tableInfoService;

    @Test
    void testGetOfficeHourPicker(){
        System.out.println(tableInfoService.getOfficeHourPicker());
    }

}
