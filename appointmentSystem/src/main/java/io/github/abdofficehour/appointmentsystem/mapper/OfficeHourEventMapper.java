package io.github.abdofficehour.appointmentsystem.mapper;

import io.github.abdofficehour.appointmentsystem.pojo.OfficeHourEvent;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OfficeHourEventMapper {
    void insertOfficeHourEvent(OfficeHourEvent officeHourEvent);

    void insertBatch(List<OfficeHourEvent> officeHourEvents);

    List<OfficeHourEvent> selectOfficeHourEventByTeacherIdAndFor14Days(String teacherId, LocalDateTime startTime, LocalDateTime endTime);
}
