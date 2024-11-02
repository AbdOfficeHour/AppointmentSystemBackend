package io.github.abdofficehour.appointmentsystem.mapper;

import io.github.abdofficehour.appointmentsystem.pojo.data.ClassroomEvent;
import io.github.abdofficehour.appointmentsystem.pojo.data.OfficeHourEvent;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ClassroomEventMapper {
    void insertClassroomEvent(ClassroomEvent classroomEvent);

    void insertClassroomEventBatch(List<ClassroomEvent> classroomEvents);

    List<ClassroomEvent> selectByIdAndTime(int id, LocalDate startDate,LocalDate endDate);

    List<ClassroomEvent> selectClassroomEventByTeacherIdAndDate(int teacherId, LocalDate dayTime);
}
