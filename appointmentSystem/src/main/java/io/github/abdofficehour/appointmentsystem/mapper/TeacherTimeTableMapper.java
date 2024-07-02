package io.github.abdofficehour.appointmentsystem.mapper;

import io.github.abdofficehour.appointmentsystem.pojo.TeacherTimeTable;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TeacherTimeTableMapper {
    List<TeacherTimeTable> selectTeacherTimeTable(@Param("teacherId") String teacherId, LocalDateTime first_appointmentDate, LocalDateTime last_appointmentDate);

    void insertBatch(@Param("timeTables") List<TeacherTimeTable> timeTables);
}
