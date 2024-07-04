package io.github.abdofficehour.appointmentsystem.mapper;

import io.github.abdofficehour.appointmentsystem.pojo.data.ClassroomTimeTable;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ClassroomTimeTableMapper {
    List<ClassroomTimeTable> selectTimeTableByTime(int id, LocalDate startDate, LocalDate endDate);
}
