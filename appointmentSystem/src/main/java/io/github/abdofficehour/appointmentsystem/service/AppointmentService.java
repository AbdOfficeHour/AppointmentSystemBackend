package io.github.abdofficehour.appointmentsystem.service;


import io.github.abdofficehour.appointmentsystem.config.Properties;
import io.github.abdofficehour.appointmentsystem.mapper.*;
import io.github.abdofficehour.appointmentsystem.pojo.data.*;
import io.github.abdofficehour.appointmentsystem.pojo.enumclass.Aim;
import io.github.abdofficehour.appointmentsystem.pojo.schema.classroomData.ClassroomEventDisplay;
import io.github.abdofficehour.appointmentsystem.pojo.schema.officehourData.OfficeHourEventDisplay;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.*;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.Period;
import io.github.abdofficehour.appointmentsystem.utils.TimeUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    AppointmentMapper appointmentMapper;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    private Properties properties;

    @Autowired
    private TeacherTimeTableMapper teacherTimeTableMapper;

    @Autowired
    private OfficeHourEventMapper officeHourEventMapper;

    @Autowired
    private ClassroomEventMapper  classroomEventMapper;

    @Autowired
    private ClassroomTimeTableMapper  classroomTimeTableMapper;

    @Autowired
    TimeUtils timeUtils;

    /**
     * 根据id,time查找OfficeHourEvents
     * @param id 对象
     * @return OfficeHourEvent对象
     */
    public List<OfficeHourEventDisplay> searchUserById(String id,int time,Boolean if_approve){
        List<OfficeHourEventDisplay> officeHourEvents;
        int howManyMonth = 0;
        if(time == 1)howManyMonth = 6;
        else if(time == 2) howManyMonth = 12;
        else if(time == 0) howManyMonth = 3;

        if(!if_approve)
            //返回时间
            officeHourEvents = appointmentMapper.findEventsByIdAndTime(id, howManyMonth);
        else
            //返回取消事件
            officeHourEvents = appointmentMapper.findEventsByIdAndTimeApprove(id);
        return officeHourEvents;
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

        // 检查当前用户是否有权限修改,暂时不需要
//        if (!event.getStudent().equals(userId) && !event.getTeacher().equals(userId)) {
//            return 101; // 权限错误
//        }

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

    public List<SelectTimeTable> getAppointmentsByTeacherId(String teacherId) {
        // 获取教师的名字
        String name = userInfoMapper.selectById(teacherId).getUsername();
        /*
         * 从数据库读取所选教师的时间表
         * 然后转化为officeHourTime字段
         */
        // 开始时间
        LocalDate today = LocalDate.now();
        // 结束时间
        LocalDate todayAfterDayLen = LocalDate.now().plusDays(properties.getDateLen());

        //查找officeHourTime
        List<TeacherTimeTable> teacherTimeTables = teacherTimeTableMapper.selectTeacherTimeTable(teacherId,today,todayAfterDayLen);
        // 将TeacherTimeTable转换为OfficeHourTime
        List<SpecialTime> specialTimes =
                teacherTimeTables.stream().map(teacherTimeTable ->
                        new SpecialTime(
                                timeUtils.toTimeStamp(teacherTimeTable.getAppointmentDate().atStartOfDay()),
                                timeUtils.toTimeStamp(teacherTimeTable.getStartTime()),
                                timeUtils.toTimeStamp(teacherTimeTable.getEndTime()))
                ).toList();

        /*
         *查找officehourevent，筛选出空余和可用
         *转换timeTable的格式，用于表示教师的空闲和非空闲时间
         */
        // 读取教师的officehourevent
        List<OfficeHourEvent> officeHourEvents = officeHourEventMapper.selectOfficeHourEventByTeacherIdAndForDayLen(teacherId, today, todayAfterDayLen);
        // 获取配置文件中的工作时间
        LocalTime defaultStartTime = LocalTime.of(properties.getStartHour(), properties.getStartMiu());
        LocalTime defaultEndTime = LocalTime.of(properties.getEndHour(), properties.getEndMiu());

        // 按日期对 officeHourEvents 进行分组，然后构建每个日期的 TimeTable
        Map<LocalDate, List<SelectPeriod>> busyPeriodsByDate = officeHourEvents.stream()
                .collect(Collectors.groupingBy(
                        OfficeHourEvent::getAppointmentDate,
                        Collectors.mapping(event -> new SelectPeriod(
                                event.getStartTime().atZone(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                event.getEndTime().atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
                        ), Collectors.toList())
                ));
        // 创建 TimeTable 列表
        List<SelectTimeTable> formatTimetable = new ArrayList<>();
        // 遍历每一天，生成 TimeTable
        for (LocalDate date = today; !date.isAfter(todayAfterDayLen); date = date.plusDays(1)) {
            // 获取当天的繁忙时间段
            List<SelectPeriod> busyPeriods = busyPeriodsByDate.getOrDefault(date, new ArrayList<>());

            // 计算当天的工作时间段
            long startOfDayTimestamp = date.atTime(defaultStartTime).toInstant(ZoneOffset.UTC).toEpochMilli();
            long endOfDayTimestamp = date.atTime(defaultEndTime).toInstant(ZoneOffset.UTC).toEpochMilli();
            List<SelectPeriod> availablePeriods = new ArrayList<>();
            availablePeriods.add(new SelectPeriod(startOfDayTimestamp, endOfDayTimestamp));

            // 减去繁忙时间段，得到最终的 available 时间段
            for (SelectPeriod busy : busyPeriods) {
                availablePeriods = subtractBusyTimeSegments(availablePeriods, busy);
            }

            // 创建 TimeTable 对象
            SelectTimeTable timeTable = new SelectTimeTable();
            timeTable.setDate(date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
            timeTable.setTimes(availablePeriods);

            // 添加到格式化的时间表列表中
            formatTimetable.add(timeTable);
        }

        return formatTimetable;
    }

    private List<SelectPeriod> subtractBusyTimeSegments(List<SelectPeriod> availablePeriods, SelectPeriod busy) {
        List<SelectPeriod> result = new ArrayList<>();

        for (SelectPeriod available : availablePeriods) {
            // 如果 busy 不在 available 范围内
            if (available.getEndTime() <= busy.getStartTime() || available.getStartTime() >= busy.getEndTime()) {
                result.add(available);
            } else {
                // 部分重叠情况：分割 available
                if (available.getStartTime() < busy.getStartTime()) {
                    result.add(new SelectPeriod(available.getStartTime(), busy.getStartTime()));
                }
                if (available.getEndTime() > busy.getEndTime()) {
                    result.add(new SelectPeriod(busy.getEndTime(), available.getEndTime()));
                }
            }
        }

        return result;
    }

    public boolean createAppointment(String student, String teacher, Map<String, Object> time, String note, String question, List<String> present) {
        try {
            long dateTimestamp = ((Number) time.get("date")).longValue();
            long startTimeTimestamp = ((Number) time.get("start_time")).longValue();
            long endTimeTimestamp = ((Number) time.get("end_time")).longValue();

            // 将日期转换为 LocalDate
            LocalDate date = Instant.ofEpochSecond(dateTimestamp).atZone(ZoneId.systemDefault()).toLocalDate();

            // 将 start_time 和 end_time 转换为 LocalTime
            LocalTime startTime = Instant.ofEpochSecond(startTimeTimestamp).atZone(ZoneId.systemDefault()).toLocalTime();
            LocalTime endTime = Instant.ofEpochSecond(endTimeTimestamp).atZone(ZoneId.systemDefault()).toLocalTime();

            // 将 LocalDate 和 LocalTime 组合为 LocalDateTime
            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

            OfficeHourEvent appointment = new OfficeHourEvent();
            appointment.setAppointmentDate(date);
            appointment.setStartTime(startDateTime);
            appointment.setEndTime(endDateTime);
            appointment.setStudent(student);
            appointment.setTeacher(teacher);
            appointment.setNote(note);
            appointment.setQuestion(question);
            appointment.setState(2);

            List<OfficeHourEvent> conflictingEvents = appointmentMapper.checkTimeConflict(teacher, date, startDateTime, endDateTime);
            if (!conflictingEvents.isEmpty()) {
                return false;
            } else {
                appointmentMapper.insertAppointment(appointment);

                int eventId = appointment.getId();
                for (String studentId : present) {
                    appointmentMapper.insertOfficeHourEventPresent(eventId, studentId);
                }
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据id,time查找ClassRoomEvents
     * @param id 对象
     * @return classroomEvent对象
     */
    public List<ClassroomEventDisplay> searchClassRoomEventById(String id, int time,Boolean if_approve){
        List<ClassroomEventDisplay> classroomeventEvent;
        int howManyMonth = 0;
        if(time == 1)howManyMonth = 6;
        else if(time == 2) howManyMonth = 12;
        else if(time == 0) howManyMonth = 3;

        if(!if_approve)
            //返回时间
            classroomeventEvent = appointmentMapper.findClassroomEventsByIdAndTime(id, howManyMonth);
        else
            //返回取消事件
            classroomeventEvent = appointmentMapper.findClassroomEventsByIdAndTimeApprove(id);
        return classroomeventEvent;
    }

    public List<ClassroomEventDisplay> searchClassRoomEvent(String userId,int time){
        List<ClassroomEventDisplay> classroomeventEvent;
        int howManyMonth = 3;
        if(time == 1)howManyMonth = 6;
        else if(time == 2) howManyMonth = 12;

        return appointmentMapper.findClassroomEvents(userId,time);
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
//        if (!event.getApplicant().equals(userId)) {
//            return 101; // 权限错误
//        }

        // 更新事件信息
        if (updateData.containsKey("state")) {
            if(event.getState() == 2){
                // 设置审批人
                event.setApprove(userId);
            }
            event.setState((int) updateData.get("state"));
        }

        // 更新数据库
        appointmentMapper.updateClassroomEvent(event);

        return 0; // 修改成功
    }

    public List<Map<String, Object>> getAvailableClassrooms() {
        List<String> classroomIds = appointmentMapper.findAllClassroomIds();
        if (classroomIds.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> classroomList = appointmentMapper.findClassroomsByIds(classroomIds);

        // 将教师信息转换成List<Map<String, String>>的形式
        return classroomList.stream()
                .map(classrooms -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("classroomID", classrooms.get("id"));
                    map.put("classroom", classrooms.get("classroom"));
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<SelectTimeTable> getAppointmentsByClassroomId(int classroomId) {
        //开始时间
        LocalDate today = LocalDate.now();
        // 结束时间
        LocalDate todayAfterDayLen = LocalDate.now().plusDays(properties.getDateLen());

        List<io.github.abdofficehour.appointmentsystem.pojo.data.ClassroomTimeTable> classroomTimeTableList = classroomTimeTableMapper.selectTimeTableByTime(classroomId,today,todayAfterDayLen);
        // 将classroomTimeTable转换为SpecialTime
        List<SpecialTime> specialTimes =
                classroomTimeTableList.stream().map(classroomTimeTable ->
                        new SpecialTime(
                                timeUtils.toTimeStamp(classroomTimeTable.getAppointmentDate().atStartOfDay()),
                                timeUtils.toTimeStamp(classroomTimeTable.getStartTime()),
                                timeUtils.toTimeStamp(classroomTimeTable.getEndTime()))
                ).toList();

        // 获取配置文件中的默认工作时间
        LocalTime defaultStartTime = LocalTime.of(properties.getStartHour(), properties.getStartMiu());
        LocalTime defaultEndTime = LocalTime.of(properties.getEndHour(), properties.getEndMiu());
        // 读取 ClassroomEvent 数据，获取指定日期范围内的事件
        List<ClassroomEvent> classroomEvents = classroomEventMapper.selectByIdAndTime(classroomId, today, todayAfterDayLen);
        // 将 classroomEvents 转换为 List<TableEvent>，表示 busy 时间段
        List<TableEvent> busyEvents = classroomEvents
                .stream()
                .map(classroomEvent -> new TableEvent(
                        classroomEvent.getAppointmentDate(),
                        classroomEvent.getStartTime(),
                        classroomEvent.getEndTime(),
                        classroomEvent.getState()
                ))
                .toList();

        // 创建 TimeTable 列表
        List<SelectTimeTable> formatTimetable = new ArrayList<>();

        // 遍历指定日期范围，生成每一天的 TimeTable
        for (LocalDate date = today; !date.isAfter(todayAfterDayLen); date = date.plusDays(1)) {
            // 获取当天的繁忙时间段（busy）
            LocalDate finalDate = date;
            List<SelectPeriod> busyPeriods = busyEvents.stream()
                    .filter(event -> event.getAppointmentDate().equals(finalDate))
                    .map(event -> new SelectPeriod(
                            event.getStartTime().atZone(ZoneOffset.UTC).toInstant().toEpochMilli(),
                            event.getEndTime().atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
                    ))
                    .toList();

            // 初始化当天的可用时间段（available），为整天的工作时间段
            long startOfDayTimestamp = date.atTime(defaultStartTime).toInstant(ZoneOffset.UTC).toEpochMilli();
            long endOfDayTimestamp = date.atTime(defaultEndTime).toInstant(ZoneOffset.UTC).toEpochMilli();
            List<SelectPeriod> availablePeriods = new ArrayList<>();
            availablePeriods.add(new SelectPeriod(startOfDayTimestamp, endOfDayTimestamp));

            // 从 available 时间段中减去 busy 时间段，得到最终的 available 时间段
            for (SelectPeriod busy : busyPeriods) {
                availablePeriods = subtractBusyTimeSegments(availablePeriods, busy);
            }

            // 创建 TimeTable 对象
            SelectTimeTable timeTable = new SelectTimeTable();
            timeTable.setDate(date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()); // 日期时间戳
            timeTable.setTimes(availablePeriods);  // 设置当天的可用时间段

            // 添加到时间表列表中
            formatTimetable.add(timeTable);
        }
        return formatTimetable;
    }


    public boolean createClassroomEvent(String userId,int classroomId, Map<String, Object> time, boolean isMedia, boolean isComputer, boolean isSound, List<String> present, String aim, String events, int state) {
        try {

            long dateTimestamp = ((Number) time.get("date")).longValue();
            long startTimeTimestamp = ((Number) time.get("start_time")).longValue();
            long endTimeTimestamp = ((Number) time.get("end_time")).longValue();

            // 将日期转换为 LocalDate
            LocalDate date = Instant.ofEpochSecond(dateTimestamp).atZone(ZoneId.systemDefault()).toLocalDate();

            // 将 start_time 和 end_time 转换为 LocalTime
            LocalTime startTime = Instant.ofEpochSecond(startTimeTimestamp).atZone(ZoneId.systemDefault()).toLocalTime();
            LocalTime endTime = Instant.ofEpochSecond(endTimeTimestamp).atZone(ZoneId.systemDefault()).toLocalTime();

            // 将 LocalDate 和 LocalTime 组合为 LocalDateTime
            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

            ClassroomEvent classroomEvent = new ClassroomEvent();
            classroomEvent.setClassroom(classroomId);
            classroomEvent.setAppointmentDate(date);
            classroomEvent.setStartTime(startDateTime);
            classroomEvent.setEndTime(endDateTime);
            classroomEvent.setApplicant(userId);
            classroomEvent.setIsMedia(isMedia);
            classroomEvent.setIsComputer(isComputer);
            classroomEvent.setIsSound(isSound);
            classroomEvent.setAim(Aim.fromValue(aim));
            classroomEvent.setApprove("");
            classroomEvent.setEvents(events);
            classroomEvent.setState(state);

            List<ClassroomEvent> conflictingEvents = appointmentMapper.checkTimeConflictInClassroom(userId, date, startDateTime, endDateTime);

            if (!conflictingEvents.isEmpty()) {
                return false;
            } else {
                appointmentMapper.insertClassroomEvent(classroomEvent);

                int eventId = classroomEvent.getId();
                for (String studentId : present) {
                    appointmentMapper.insertClassroomEventPresent(eventId, studentId);
                }

                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
