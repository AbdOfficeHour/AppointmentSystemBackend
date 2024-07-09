package io.github.abdofficehour.appointmentsystem.mapper;

import io.github.abdofficehour.appointmentsystem.pojo.data.OfficeHourEvent;
import io.github.abdofficehour.appointmentsystem.pojo.data.TeacherClassification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AppointmentMapper {
    /**
     * 查找学生或教师的预约记录，并根据时间筛选
     *
     * @param id   学生或教师的姓名
     * @param time 筛选的时间段，单位：月
     * @return 符合条件的预约记录列表
     */
    List<OfficeHourEvent> findEventsByIdAndTime(@Param("id") String id, @Param("time") int time);

    List<String> findEventsById(int id);//这个是用于get时找同伙用的

    void updateEvent(OfficeHourEvent event);

    OfficeHourEvent findEventById(@Param("id") int id);

    List<String> findAllTeacherIds();

    List<Map<String, String>> findTeacherNamesByIds(@Param("ids") List<String> ids);

    List<Map<String, Object>> findAppointmentsByTeacherId(@Param("teacherId") String teacherId);

    void insertAppointment(@Param("appointment") OfficeHourEvent appointment);

}
