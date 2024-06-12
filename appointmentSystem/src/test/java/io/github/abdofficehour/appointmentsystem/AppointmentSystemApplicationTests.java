package io.github.abdofficehour.appointmentsystem;

import io.github.abdofficehour.appointmentsystem.mapper.ClassroomEventMapper;
import io.github.abdofficehour.appointmentsystem.mapper.OfficeHourEventMapper;
import io.github.abdofficehour.appointmentsystem.mapper.UserInfoMapper;
import io.github.abdofficehour.appointmentsystem.pojo.ClassroomEvent;
import io.github.abdofficehour.appointmentsystem.pojo.OfficeHourEvent;
import io.github.abdofficehour.appointmentsystem.pojo.enumclass.Aim;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
class AppointmentSystemApplicationTests {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    OfficeHourEventMapper officeHourEventMapper;

    @Autowired
    ClassroomEventMapper classroomEventMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void testMybatis(){
        System.out.println(userInfoMapper.findAll());
    }

    @Test
    void testEnum(){
        OfficeHourEvent officeHourEvent = new OfficeHourEvent();
        officeHourEvent.setAppointmentDate(LocalDate.now());
        officeHourEvent.setStartTime(LocalDateTime.now());
        officeHourEvent.setEndTime(LocalDateTime.now());
        officeHourEvent.setStudent("1");
        officeHourEvent.setTeacher("2");
        officeHourEvent.setNote("aaa");
        officeHourEvent.setQuestion("bbb");
        officeHourEvent.setRefuseResult("");
        officeHourEvent.setWorkSummary("CCC");
        officeHourEvent.setState(1);

        officeHourEventMapper.insertOfficeHourEvent(officeHourEvent);

    }

    @Test
    void testClassroomEvent(){
        ClassroomEvent classroomEvent = new ClassroomEvent();
        classroomEvent.setAppointmentDate(LocalDate.now());
        classroomEvent.setStartTime(LocalDateTime.now());
        classroomEvent.setEndTime(LocalDateTime.now());
        classroomEvent.setApplicant("1");
        classroomEvent.setClassroom(1);
        classroomEvent.setIsComputer(false);
        classroomEvent.setIsSound(false);
        classroomEvent.setIsMedia(false);
        classroomEvent.setAim(Aim.DISCUSS);
        classroomEvent.setEvents("aaa");
        classroomEvent.setApprove("bbb");
        classroomEvent.setState(1);

        classroomEventMapper.insertClassroomEvent(classroomEvent);
    }

}
