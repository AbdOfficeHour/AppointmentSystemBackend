package io.github.abdofficehour.appointmentsystem.service;

import io.github.abdofficehour.appointmentsystem.config.Properties;
import io.github.abdofficehour.appointmentsystem.mapper.*;
import io.github.abdofficehour.appointmentsystem.pojo.data.ClassroomEvent;
import io.github.abdofficehour.appointmentsystem.pojo.data.OfficeHourEvent;
import io.github.abdofficehour.appointmentsystem.pojo.data.TeacherBanTime;
import io.github.abdofficehour.appointmentsystem.pojo.data.TeacherTimeTable;
import io.github.abdofficehour.appointmentsystem.pojo.enumclass.Aim;
import io.github.abdofficehour.appointmentsystem.pojo.schema.classroomClassification.ClassroomClassificationSchema;
import io.github.abdofficehour.appointmentsystem.pojo.schema.classroomClassification.ClassroomsInClassification;
import io.github.abdofficehour.appointmentsystem.pojo.schema.teacherClassification.TeacherClassificationSchema;
import io.github.abdofficehour.appointmentsystem.pojo.schema.teacherClassification.TeachersInClassification;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.*;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.Period;
import io.github.abdofficehour.appointmentsystem.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TableInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private ClassroomMapper classroomMapper;

    @Autowired
    private ClassroomTimeTableMapper classroomTimeTableMapper;

    @Autowired
    private ClassroomEventMapper classroomEventMapper;

    @Autowired
    private TeacherTimeTableMapper teacherTimeTableMapper;

    @Autowired
    private OfficeHourEventMapper officeHourEventMapper;

    @Autowired
    private Properties properties;

    @Autowired
    private TimeUtils timeUtils;

    /**
     * 获取officehour选择器
     * @return TeacherClassificationSchema列表 用于记录officeHour教师类型对应教师
     */
    public List<TeachersInClassification> getOfficeHourPicker(){
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(properties.getDateLen());

        // 读取所有的teacher相关的classification
        List<TeacherClassificationSchema> teacherClassificationSchemas = userInfoMapper.selectAllClassification(startDate,endDate);
        // 用于存放teacher的信息
        Map<String, TeachersInClassification> teacherMap = new HashMap<>();
        for(TeacherClassificationSchema teacherClassificationSchema : teacherClassificationSchemas){

            if(!teacherMap.containsKey(teacherClassificationSchema.getClassification())){
                // 如果teacherMap中没有这个classification，就新建一个TeacherClassificationSchema
                TeachersInClassification teachersInClassification = new TeachersInClassification();
                teachersInClassification.setClassification(teacherClassificationSchema.getClassification());
                // 读取teacher的信息
                Map<String,Object> teachers = new HashMap<>();
                teachers.put("teacherId", teacherClassificationSchema.getTeacherId());
                teachers.put("teacherName", teacherClassificationSchema.getTeacherName());
                // 将teacher信息放入TeacherClassificationSchema
                teachersInClassification.setTeachers(new ArrayList<>(){{add(teachers);}});
                teacherMap.put(teacherClassificationSchema.getClassification(), teachersInClassification);
            }else{
                // 如果teacherMap中有这个classification，就直接读取出来,并将teacher信息放入
                teacherMap.get(teacherClassificationSchema.getClassification()).getTeachers().add(new HashMap<>(){{
                    put("teacherId", teacherClassificationSchema.getTeacherId());
                    put("teacherName", teacherClassificationSchema.getTeacherName());
                }});
            }
        }
        return teacherMap.values().stream().toList();
    }

    /**
     * 获取classroom选择器
     * @return ClassroomsInClassification列表用于记录教室类型
     */
    public List<ClassroomsInClassification> getClassroomPicker(){
        // 读取所有教室种类
        List<ClassroomClassificationSchema> classificationList = classroomMapper.selectClassification();
        // 用于存放教室信息
        Map<String, ClassroomsInClassification> classroomsInClassificationMap = new HashMap<>();

        // 开始遍历
        for(ClassroomClassificationSchema iterClassroom :classificationList){
            if(!classroomsInClassificationMap.containsKey(iterClassroom.getClassification())){
                // 如果没有就创建
                ClassroomsInClassification classroomsInClassification = new ClassroomsInClassification();
                classroomsInClassification.setClassification(iterClassroom.getClassification());

                // 读取classroom的信息
                Map<String,Object> classrooms = new HashMap<>();
                classrooms.put("classroomId", iterClassroom.getClassroomId());
                classrooms.put("classroom", iterClassroom.getClassroom());
                // 将classroom信息放入classroomClassificationSchema
                classroomsInClassification.setClassrooms(new ArrayList<>(){{add(classrooms);}});
                classroomsInClassificationMap.put(iterClassroom.getClassification(), classroomsInClassification);
            }else{
                classroomsInClassificationMap.get(iterClassroom.getClassification()).getClassrooms().add(new HashMap<>(){{
                    put("classroomId", iterClassroom.getClassroomId());
                    put("classroom", iterClassroom.getClassroom());
                }});
            }
        }

        return classroomsInClassificationMap.values().stream().toList();
    }

    /**
     * 获取指定教师的时间表
     * 筛选officehour时间，并且标记已存在时间
     * @param teacherId 教师id
     */
    public OfficeHourTimetable getTeacherTimeTable(String teacherId){
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
        Map<LocalDate, List<Period>> busyPeriodsByDate = officeHourEvents.stream()
                .collect(Collectors.groupingBy(
                        OfficeHourEvent::getAppointmentDate,
                        Collectors.mapping(event -> new Period(
                                event.getStartTime().atZone(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                event.getEndTime().atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
                        ), Collectors.toList())
                ));
        // 创建 TimeTable 列表
        List<TimeTable> formatTimetable = new ArrayList<>();
        // 遍历每一天，生成 TimeTable
        for (LocalDate date = today; !date.isAfter(todayAfterDayLen); date = date.plusDays(1)) {
            // 获取当天的繁忙时间段
            List<Period> busyPeriods = busyPeriodsByDate.getOrDefault(date, new ArrayList<>());

            // 计算当天的工作时间段
            long startOfDayTimestamp = date.atTime(defaultStartTime).toInstant(ZoneOffset.UTC).toEpochMilli();
            long endOfDayTimestamp = date.atTime(defaultEndTime).toInstant(ZoneOffset.UTC).toEpochMilli();
            List<Period> availablePeriods = new ArrayList<>();
            availablePeriods.add(new Period(startOfDayTimestamp, endOfDayTimestamp));

            // 减去繁忙时间段，得到最终的 available 时间段
            for (Period busy : busyPeriods) {
                availablePeriods = subtractBusyTimeSegments(availablePeriods, busy);
            }

            // 创建 TimeTable 对象
            TimeTable timeTable = new TimeTable();
            timeTable.setDate(date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
            timeTable.setBusy(busyPeriods);
            timeTable.setAvailable(availablePeriods);

            // 添加到格式化的时间表列表中
            formatTimetable.add(timeTable);
        }
        return new OfficeHourTimetable(name, specialTimes,formatTimetable);
    }

    private List<Period> subtractBusyTimeSegments(List<Period> availablePeriods, Period busy) {
        List<Period> result = new ArrayList<>();

        for (Period available : availablePeriods) {
            // 如果 busy 不在 available 范围内
            if (available.getEnd() <= busy.getStart() || available.getStart() >= busy.getEnd()) {
                result.add(available);
            } else {
                // 部分重叠情况：分割 available
                if (available.getStart() < busy.getStart()) {
                    result.add(new Period(available.getStart(), busy.getStart()));
                }
                if (available.getEnd() > busy.getEnd()) {
                    result.add(new Period(busy.getEnd(), available.getEnd()));
                }
            }
        }

        return result;
    }


    /**
     * 获取classroom时间表
     * @param classroomId 教室id
     * @return ClassroomTimeTable 表示该教室的从今天开始往后一定时间的占用情况
     */
    public ClassroomTimeTable getClassroomTimeTable(int classroomId){

        //拿取教室名
        String name = classroomMapper.selectById(classroomId).getClassroomName();

        /*
         * 从数据库读取所选教室的时间表
         *
         */
        // 开始时间
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
        List<TimeTable> formatTimetable = new ArrayList<>();

        // 遍历指定日期范围，生成每一天的 TimeTable
        for (LocalDate date = today; !date.isAfter(todayAfterDayLen); date = date.plusDays(1)) {
            // 获取当天的繁忙时间段（busy）
            LocalDate finalDate = date;
            List<Period> busyPeriods = busyEvents.stream()
                    .filter(event -> event.getAppointmentDate().equals(finalDate))
                    .map(event -> new Period(
                            event.getStartTime().atZone(ZoneOffset.UTC).toInstant().toEpochMilli(),
                            event.getEndTime().atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
                    ))
                    .toList();

            // 初始化当天的可用时间段（available），为整天的工作时间段
            long startOfDayTimestamp = date.atTime(defaultStartTime).toInstant(ZoneOffset.UTC).toEpochMilli();
            long endOfDayTimestamp = date.atTime(defaultEndTime).toInstant(ZoneOffset.UTC).toEpochMilli();
            List<Period> availablePeriods = new ArrayList<>();
            availablePeriods.add(new Period(startOfDayTimestamp, endOfDayTimestamp));

            // 从 available 时间段中减去 busy 时间段，得到最终的 available 时间段
            for (Period busy : busyPeriods) {
                availablePeriods = subtractBusyTimeSegments(availablePeriods, busy);
            }

            // 创建 TimeTable 对象
            TimeTable timeTable = new TimeTable();
            timeTable.setDate(date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()); // 日期时间戳
            timeTable.setBusy(busyPeriods);  // 设置当天的繁忙时间段
            timeTable.setAvailable(availablePeriods);  // 设置当天的可用时间段

            // 添加到时间表列表中
            formatTimetable.add(timeTable);
        }

// 返回包含教室时间表的对象
        return new ClassroomTimeTable(name, specialTimes, formatTimetable);

    }

    /**
     * 用于格式化时间表，可以同时作用于officeHour和教室预约，进行格式的修改
     * @return TimeTable对象
     */
    public List<TimeTable> formatTimetable(LocalDate startDate,LocalDate endDate, List<TableEvent> tableEvents){
        // 对officeHourEvent进行预处理
        // 使用map记录每一个日期对应的officeHourEvent
        Map<Long,List<TableEvent>> dateMapTableEvent = new HashMap<>();
        for (TableEvent tableEvent: tableEvents){
            Long eventDate = timeUtils.toTimeStamp(tableEvent.getAppointmentDate().atStartOfDay());
            if(tableEvent.getState() == 6)continue;

            if(dateMapTableEvent.containsKey(eventDate)){
                // 如果这个日期存在的话
                dateMapTableEvent.get(eventDate).add(tableEvent);
            }else{
                // 不存在则创建
                // 利用静态块进行list的初始化
                dateMapTableEvent.put(eventDate,new ArrayList<>() {{add(tableEvent);}});
            }
        }

        // 最后返回的结果
        List<TimeTable> resultTimeTable = new ArrayList<>();

        // 遍历所有的specialTime
        for (LocalDate iterDate = startDate; !iterDate.isAfter(endDate); iterDate = iterDate.plusDays(1)){
            TimeTable thisDateTimetable = new TimeTable();

            // 构造一天的开始与结束
            LocalDateTime startOfToday = iterDate.atTime(properties.getStartHour(),properties.getStartMiu());
            LocalDateTime endOfToday = iterDate.atTime(properties.getEndHour(),properties.getEndMiu());

            // 设置日期
            thisDateTimetable.setDate(timeUtils.toTimeStamp(iterDate.atStartOfDay()));

            // 查询对应日期的事件
            List<TableEvent> thisTableEvent = dateMapTableEvent.get(timeUtils.toTimeStamp(iterDate.atStartOfDay()));

            // 不存在事件的情况
            if (Objects.isNull(thisTableEvent)){
                List<Period> busyTime = new ArrayList<>();
                List<Period> availableTime = new ArrayList<>(){{
                    add(new Period(timeUtils.toTimeStamp(startOfToday),timeUtils.toTimeStamp(endOfToday)));
                }};

                thisDateTimetable.setBusy(busyTime);
                thisDateTimetable.setAvailable(availableTime);
                resultTimeTable.add(thisDateTimetable);
                continue;
            }

            // 先排个序
            thisTableEvent.sort(Comparator.comparing(TableEvent::getStartTime));

            // 获取繁忙时间
            List<Period> busyTime = thisTableEvent.stream()
                    .map(officeHourEvent -> new Period(
                            timeUtils.toTimeStamp(officeHourEvent.getStartTime()),
                            timeUtils.toTimeStamp(officeHourEvent.getEndTime())
                    ))
                    .toList();

            // 获取空闲时间，这就有一点小麻烦了
            List<Period> availableTime = new ArrayList<>();

            // 特殊情况，如果有busy time的话，就从officeHourTime开始
            Period firstAvailablePeriod = new Period();
            firstAvailablePeriod.setStart(timeUtils.toTimeStamp(startOfToday));
            firstAvailablePeriod.setEnd(busyTime.get(0).getStart());
            availableTime.add(firstAvailablePeriod);

            for(int i = 1; i < busyTime.size();i++){
                // 上一个的结束和这一个的开始就是
                Period availablePeriod = new Period();
                availablePeriod.setStart(busyTime.get(i-1).getEnd());
                availablePeriod.setEnd(busyTime.get(i).getStart());
                availableTime.add(availablePeriod);
            }

            // 同理结束时间也一样
            Period endAvailablePeriod = new Period();
            endAvailablePeriod.setStart(busyTime.get(busyTime.size()-1).getEnd());
            endAvailablePeriod.setEnd(timeUtils.toTimeStamp(endOfToday));
            availableTime.add(endAvailablePeriod);

            thisDateTimetable.setBusy(busyTime);
            thisDateTimetable.setAvailable(availableTime);

            resultTimeTable.add(thisDateTimetable);
        }

        return resultTimeTable;
    }

    /**
     * 用于教师禁用时间段------------from：ymz方便修改
     */
    public List<Map<String, Object>> banTeacher(String teacherId, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime startTime, LocalDateTime endTime) {

        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today.atStartOfDay())) {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> conflict = new HashMap<>();
            conflict.put("start_date", today.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
            result.add(conflict);
            return result;
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
        List<OfficeHourEvent> officeHourEvents = new ArrayList<>();

        // 查询每一天是否存在冲突
        for (int i = 0; i <= daysBetween; i++) {
            LocalDate tempDate = startDate.toLocalDate().plusDays(i);
            List<OfficeHourEvent> newOfficeHourEvents = officeHourEventMapper.selectOfficeHourEventByTeacherIdAndDate(teacherId, tempDate);
            officeHourEvents.addAll(newOfficeHourEvents);
        }

        // 如果没有冲突的事件
        if (officeHourEvents.isEmpty()) {
            for (int i = 0; i <= daysBetween; i++) {
                LocalDate tempDate = startDate.toLocalDate().plusDays(i);
                LocalDateTime modifiedStart = startTime.withYear(tempDate.getYear())
                        .withMonth(tempDate.getMonthValue())
                        .withDayOfMonth(tempDate.getDayOfMonth());
                LocalDateTime modifiedEnd = endTime.withYear(tempDate.getYear())
                        .withMonth(tempDate.getMonthValue())
                        .withDayOfMonth(tempDate.getDayOfMonth());

                // 创建新的 OfficeHourEvent 实例，避免重复使用同一实例
                OfficeHourEvent banTime = new OfficeHourEvent();
                banTime.setTeacher(teacherId);
                banTime.setStudent("banner");
                banTime.setState(0);
                banTime.setStartTime(modifiedStart);
                banTime.setEndTime(modifiedEnd);
                banTime.setAppointmentDate(tempDate);

                // 插入禁用事件
                officeHourEventMapper.insertOfficeHourEvent(banTime);
            }
            return null;
        } else {
            // 如果有冲突，将冲突的日期、开始时间和结束时间记录
            List<Map<String, Object>> conflictPeriods = new ArrayList<>();
            for (OfficeHourEvent event : officeHourEvents) {
                Map<String, Object> conflict = new HashMap<>();
                conflict.put("date", event.getAppointmentDate().atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
                conflict.put("startTime", event.getStartTime().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
                conflict.put("endTime", event.getEndTime().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
                conflictPeriods.add(conflict);
            }
            return conflictPeriods;
        }
    }


    /**
     * 用于教室禁用时间段------------from：ymz方便修改
     */
    public List<Map<String, Object>> banClassroom(int classroomId, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime startTime, LocalDateTime endTime, String userId) {

        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today.atStartOfDay())) {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> conflict = new HashMap<>();
            conflict.put("start_date", today.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
            result.add(conflict);
            return result;
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
        List<ClassroomEvent> classroomEvents = new ArrayList<>();

        // 查询每一天是否存在冲突
        for (int i = 0; i <= daysBetween; i++) {
            LocalDate tempDate = startDate.toLocalDate().plusDays(i);
            List<ClassroomEvent> newClassroomEvents = classroomEventMapper.selectClassroomEventByTeacherIdAndDate(classroomId, tempDate);
            classroomEvents.addAll(newClassroomEvents);
        }

        // 如果没有冲突的事件
        if (classroomEvents.isEmpty()) {
            for (int i = 0; i <= daysBetween; i++) {
                LocalDate tempDate = startDate.toLocalDate().plusDays(i);
                LocalDateTime modifiedStart = startTime.withYear(tempDate.getYear())
                        .withMonth(tempDate.getMonthValue())
                        .withDayOfMonth(tempDate.getDayOfMonth());
                LocalDateTime modifiedEnd = endTime.withYear(tempDate.getYear())
                        .withMonth(tempDate.getMonthValue())
                        .withDayOfMonth(tempDate.getDayOfMonth());

                // 创建新的 OfficeHourEvent 实例，避免重复使用同一实例
                ClassroomEvent banTime = new ClassroomEvent();

                banTime.setClassroom(classroomId);
                banTime.setApplicant(userId);
                banTime.setApprove("banner");
                banTime.setAim(Aim.BANNING);
                banTime.setIsComputer(false);
                banTime.setIsMedia(false);
                banTime.setIsSound(false);
                banTime.setState(0);
                banTime.setStartTime(modifiedStart);
                banTime.setEndTime(modifiedEnd);
                banTime.setAppointmentDate(tempDate);

                // 插入禁用事件
                classroomEventMapper.insertClassroomEvent(banTime);
            }
            return null;
        } else {
            // 如果有冲突，将冲突的日期、开始时间和结束时间记录
            List<Map<String, Object>> conflictPeriods = new ArrayList<>();
            for (ClassroomEvent event : classroomEvents) {
                Map<String, Object> conflict = new HashMap<>();
                conflict.put("date", event.getAppointmentDate().atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
                conflict.put("startTime", event.getStartTime().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
                conflict.put("endTime", event.getEndTime().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
                conflictPeriods.add(conflict);
            }
            return conflictPeriods;
        }
    }
}
