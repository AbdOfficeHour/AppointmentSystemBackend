package io.github.abdofficehour.appointmentsystem;

import io.github.abdofficehour.appointmentsystem.mapper.UserInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AppointmentSystemApplicationTests {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void testMybatis(){
        System.out.println(userInfoMapper.findAll());
    }

}
