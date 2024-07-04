package io.github.abdofficehour.appointmentsystem.mapper;

import io.github.abdofficehour.appointmentsystem.pojo.data.TeacherTimeTable;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface TeacherTimeTableMapper {
    List<TeacherTimeTable> selectTeacherTimeTable(@Param("teacherId") String teacherId, LocalDate first_appointmentDate, LocalDate last_appointmentDate);

    void insertBatch(@Param("timeTables") List<TeacherTimeTable> timeTables);
}
