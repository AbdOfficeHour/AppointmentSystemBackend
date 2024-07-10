package io.github.abdofficehour.appointmentsystem;

import io.github.abdofficehour.appointmentsystem.utils.TimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TimeUtilsTest {

    @Autowired
    private TimeUtils timeUtils;

    @Test
    public void testTimeUtils(){
        System.out.println(timeUtils.DateTimeFromTimeStamp(1720713600000L));
    }

}
