package io.github.abdofficehour.appointmentsystem.mapper;

import io.github.abdofficehour.appointmentsystem.pojo.OfficeHourEvent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OfficeHourEventMapper {
    public void insertOfficeHourEvent(OfficeHourEvent officeHourEvent);
}
