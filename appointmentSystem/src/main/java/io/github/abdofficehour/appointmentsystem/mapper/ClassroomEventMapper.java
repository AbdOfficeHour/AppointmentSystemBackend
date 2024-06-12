package io.github.abdofficehour.appointmentsystem.mapper;

import io.github.abdofficehour.appointmentsystem.pojo.ClassroomEvent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClassroomEventMapper {
    public void insertClassroomEvent(ClassroomEvent classroomEvent);
}
