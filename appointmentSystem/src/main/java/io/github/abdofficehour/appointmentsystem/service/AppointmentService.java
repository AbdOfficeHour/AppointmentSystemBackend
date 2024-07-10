package io.github.abdofficehour.appointmentsystem.service;


import io.github.abdofficehour.appointmentsystem.mapper.AppointmentMapper;
import io.github.abdofficehour.appointmentsystem.mapper.OfficeHourEventMapper;
import io.github.abdofficehour.appointmentsystem.mapper.UserInfoMapper;
import io.github.abdofficehour.appointmentsystem.pojo.data.ClassroomEvent;
import io.github.abdofficehour.appointmentsystem.pojo.data.OfficeHourEvent;
import io.github.abdofficehour.appointmentsystem.pojo.data.TeacherClassification;
import io.github.abdofficehour.appointmentsystem.pojo.data.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    AppointmentMapper appointmentMapper;

    @Autowired
    UserInfoMapper userInfoMapper;

    /**
     * 根据id,time查找OfficeHourEvents
     * @param id 对象
     * @return OfficeHourEvent对象
     */
    public List<OfficeHourEvent> searchUserById(String id,int time){
        List<OfficeHourEvent> officeHourEvent;
        officeHourEvent = appointmentMapper.findEventsByIdAndTime(id, time);
        return officeHourEvent;
    }


    public List<String> searchById(int id){
        return appointmentMapper.findEventsById(id);
    }

    public int updateOfficeHourEvent(int id, Map<String, Object> updateData, String userId) {
        // 根据id获取事件信息
        OfficeHourEvent event = appointmentMapper.findEventById(id);

        if (event == null) {
            return 102; // 事件不存在
        }

        // 检查当前用户是否有权限修改
        if (!event.getStudent().equals(userId) && !event.getTeacher().equals(userId)) {
            return 101; // 权限错误
        }

        // 更新事件信息
        if (updateData.containsKey("state")) {
            event.setState((int) updateData.get("state"));
        }
        if (updateData.containsKey("refuse_result")) {
            event.setRefuseResult((String) updateData.get("refuse_result"));
        }
        if (updateData.containsKey("work_summary")) {
            event.setWorkSummary((String) updateData.get("work_summary"));
        }

        // 更新数据库
        appointmentMapper.updateEvent(event);

        return 0; // 修改成功
    }

    public List<Map<String, String>> getAvailableTeachers() {
        List<String> teacherIds = appointmentMapper.findAllTeacherIds();
        if (teacherIds.isEmpty()) {
            return null;
        }
        List<Map<String, String>> teacherList = appointmentMapper.findTeacherNamesByIds(teacherIds);

        // 将教师信息转换成List<Map<String, String>>的形式
        return teacherList.stream()
                .map(teacher -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("teacherID", teacher.get("teacherID"));
                    map.put("teacherName", teacher.get("teacherName"));
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAppointmentsByTeacherId(String teacherId) {
        List<Map<String, Object>> appointments = appointmentMapper.findAppointmentsByTeacherId(teacherId);
        Map<Long, Map<String, Object>> dateTimeMap = new HashMap<>();

        for (Map<String, Object> appointment : appointments) {
            Instant appointmentDate = ((java.sql.Date) appointment.get("appointmentDate")).toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC);
            long dateTimestamp = appointmentDate.getEpochSecond();
            Instant startTime = ((java.sql.Timestamp) appointment.get("startTime")).toInstant();
            Instant endTime = ((java.sql.Timestamp) appointment.get("endTime")).toInstant();
            long startTimeTimestamp = startTime.getEpochSecond();
            long endTimeTimestamp = endTime.getEpochSecond();

            Map<String, Object> dateMap = dateTimeMap.getOrDefault(dateTimestamp, new HashMap<>());
            dateMap.put("date", dateTimestamp);
            List<Map<String, Long>> times = (List<Map<String, Long>>) dateMap.getOrDefault("times", new ArrayList<>());

            Map<String, Long> timePeriod = new HashMap<>();
            timePeriod.put("startTime", startTimeTimestamp);
            timePeriod.put("endTime", endTimeTimestamp);
            times.add(timePeriod);
            dateMap.put("times", times);

            dateTimeMap.put(dateTimestamp, dateMap);
        }

        return new ArrayList<>(dateTimeMap.values());
    }

    public boolean createAppointment(String student, String teacher, Map<String, Object> time, String note, String question) {
        try {
            long dateTimestamp = ((Number) time.get("date")).longValue();
            long startTimeTimestamp = ((Number) time.get("start_time")).longValue();
            long endTimeTimestamp = ((Number) time.get("end_time")).longValue();

            LocalDate appointmentDate = Instant.ofEpochSecond(dateTimestamp).atZone(ZoneOffset.UTC).toLocalDate();
            LocalDateTime startTime = Instant.ofEpochSecond(startTimeTimestamp).atZone(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime endTime = Instant.ofEpochSecond(endTimeTimestamp).atZone(ZoneOffset.UTC).toLocalDateTime();

            OfficeHourEvent appointment = new OfficeHourEvent();
            appointment.setAppointmentDate(appointmentDate);
            appointment.setStartTime(startTime);
            appointment.setEndTime(endTime);
            appointment.setStudent(student);
            appointment.setTeacher(teacher);
            appointment.setNote(note);
            appointment.setQuestion(question);
            appointment.setState(2);

            appointmentMapper.insertAppointment(appointment);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据id,time查找ClassRoomEvents
     * @param id 对象
     * @return classroomEvent对象
     */
    public List<ClassroomEvent> searchClassRoomEventById(String id, int time){
        List<ClassroomEvent> classroomeventEvent;
        classroomeventEvent = appointmentMapper.findClassroomEventsByIdAndTime(id, time);
        return classroomeventEvent;
    }

    public List<String> searchClassroomById(int id){
        return appointmentMapper.searchClassroomById(id);
    }

    public int updateClassroomEvent(int id, Map<String, Object> updateData, String userId) {
        // 根据id获取事件信息
        ClassroomEvent event = appointmentMapper.findClassroomEventById(id);

        if (event == null) {
            return 102; // 事件不存在
        }

        // 检查当前用户是否有权限修改
        if (!event.getApplicant().equals(userId)) {
            return 101; // 权限错误
        }

        // 更新事件信息
        if (updateData.containsKey("state")) {
            event.setState((int) updateData.get("state"));
        }

        // 更新数据库
        appointmentMapper.updateClassroomEvent(event);

        return 0; // 修改成功
    }

    public List<Map<String, String>> getAvailableClassrooms() {
        List<String> classroomIds = appointmentMapper.findAllClassroomIds();
        if (classroomIds.isEmpty()) {
            return null;
        }
        List<Map<String, String>> classroomList = appointmentMapper.findClassroomsByIds(classroomIds);

        // 将教师信息转换成List<Map<String, String>>的形式
        return classroomList.stream()
                .map(classrooms -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("classroomId", classrooms.get("teacherID"));
                    map.put("classroom", classrooms.get("teacherName"));
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAppointmentsByClassroomId(String classroomId) {
        List<Map<String, Object>> appointments = appointmentMapper.findAppointmentsByClassroomId(classroomId);
        Map<Long, Map<String, Object>> dateTimeMap = new HashMap<>();

        for (Map<String, Object> appointment : appointments) {
            Instant appointmentDate = ((java.sql.Date) appointment.get("appointmentDate")).toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC);
            long dateTimestamp = appointmentDate.getEpochSecond();
            Instant startTime = ((java.sql.Timestamp) appointment.get("startTime")).toInstant();
            Instant endTime = ((java.sql.Timestamp) appointment.get("endTime")).toInstant();
            long startTimeTimestamp = startTime.getEpochSecond();
            long endTimeTimestamp = endTime.getEpochSecond();

            Map<String, Object> dateMap = dateTimeMap.getOrDefault(dateTimestamp, new HashMap<>());
            dateMap.put("date", dateTimestamp);
            List<Map<String, Long>> times = (List<Map<String, Long>>) dateMap.getOrDefault("times", new ArrayList<>());

            Map<String, Long> timePeriod = new HashMap<>();
            timePeriod.put("startTime", startTimeTimestamp);
            timePeriod.put("endTime", endTimeTimestamp);
            times.add(timePeriod);
            dateMap.put("times", times);

            dateTimeMap.put(dateTimestamp, dateMap);
        }

        return new ArrayList<>(dateTimeMap.values());
    }

    public boolean createClassroomEvent(String classroom, Map<String, Object> time, boolean isMedia, boolean isComputer, boolean isSound, String present, String aim, String events, int state) {
        try {
            long dateTimestamp = ((Number) time.get("date")).longValue();
            long startTimeTimestamp = ((Number) time.get("start_time")).longValue();
            long endTimeTimestamp = ((Number) time.get("end_time")).longValue();

            LocalDate appointmentDate = Instant.ofEpochSecond(dateTimestamp).atZone(ZoneOffset.UTC).toLocalDate();
            LocalDateTime startTime = Instant.ofEpochSecond(startTimeTimestamp).atZone(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime endTime = Instant.ofEpochSecond(endTimeTimestamp).atZone(ZoneOffset.UTC).toLocalDateTime();

            ClassroomEvent classroomEvent = new ClassroomEvent();
//            classroomEvent.setClassroom(classroom);
            classroomEvent.setAppointmentDate(appointmentDate);
            classroomEvent.setStartTime(startTime);
            classroomEvent.setEndTime(endTime);
            classroomEvent.setIsMedia(isMedia);
            classroomEvent.setIsComputer(isComputer);
            classroomEvent.setIsSound(isSound);
//            classroomEvent.setPresent(present);
//            classroomEvent.setAim(aim);
            classroomEvent.setEvents(events);
            classroomEvent.setState(state);

//            AppointmentMapper.insertClassroomEvent(classroomEvent);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
