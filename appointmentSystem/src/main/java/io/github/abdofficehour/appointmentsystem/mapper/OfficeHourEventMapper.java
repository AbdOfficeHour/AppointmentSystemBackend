package io.github.abdofficehour.appointmentsystem.mapper;

import io.github.abdofficehour.appointmentsystem.pojo.data.OfficeHourEvent;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OfficeHourEventMapper {
    void insertOfficeHourEvent(OfficeHourEvent officeHourEvent);

    void insertBatch(List<OfficeHourEvent> officeHourEvents);

    List<OfficeHourEvent> selectOfficeHourEventByTeacherIdAndForDayLen(String teacherId, LocalDate startTime, LocalDate endTime);
}
