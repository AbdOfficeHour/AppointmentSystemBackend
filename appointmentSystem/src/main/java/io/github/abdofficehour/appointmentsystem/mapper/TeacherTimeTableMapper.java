package io.github.abdofficehour.appointmentsystem.mapper;

import io.github.abdofficehour.appointmentsystem.pojo.TeacherTimeTable;

import java.time.LocalDateTime;
import java.util.List;

public interface TeacherTimeTableMapper {
    List<TeacherTimeTable> selectTeacherTimeTable(String teacherId, LocalDateTime first_appointmentDate, LocalDateTime last_appointmentDate);
}
