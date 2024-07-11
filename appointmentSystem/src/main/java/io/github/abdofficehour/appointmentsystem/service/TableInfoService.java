package io.github.abdofficehour.appointmentsystem.service;

import io.github.abdofficehour.appointmentsystem.config.Properties;
import io.github.abdofficehour.appointmentsystem.mapper.*;
import io.github.abdofficehour.appointmentsystem.pojo.data.ClassroomEvent;
import io.github.abdofficehour.appointmentsystem.pojo.data.OfficeHourEvent;
import io.github.abdofficehour.appointmentsystem.pojo.data.TeacherBanTime;
import io.github.abdofficehour.appointmentsystem.pojo.data.TeacherTimeTable;
import io.github.abdofficehour.appointmentsystem.pojo.schema.classroomClassification.ClassroomClassificationSchema;
import io.github.abdofficehour.appointmentsystem.pojo.schema.classroomClassification.ClassroomsInClassification;
import io.github.abdofficehour.appointmentsystem.pojo.schema.teacherClassification.TeacherClassificationSchema;
import io.github.abdofficehour.appointmentsystem.pojo.schema.teacherClassification.TeachersInClassification;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.*;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.Period;
import io.github.abdofficehour.appointmentsystem.utils.TimeUtils;
import org.apache.ibatis.type.NStringTypeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
        // 读取教师的时间表
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
        转换timeTable的格式，用于表示教师的空闲和非空闲时间
         */

        // 读取教师的officehour
        List<OfficeHourEvent> officeHourEvents = officeHourEventMapper.selectOfficeHourEventByTeacherIdAndForDayLen(teacherId, today, todayAfterDayLen);

        // 获取的最终timeTable格式
        List<TimeTable> formatTimetable = formatTimetable(
                today,
                todayAfterDayLen,
                officeHourEvents
                        .stream()
                        .map(officeHourEvent -> new TableEvent(
                                officeHourEvent.getAppointmentDate(),
                                officeHourEvent.getStartTime(),
                                officeHourEvent.getEndTime(),
                                officeHourEvent.getState()
                        ))
                        .toList()
        );

        return new OfficeHourTimetable(name, specialTimes,formatTimetable);
    }

    /**
     * 获取classroom时间表
     * @param classroomId 教室id
     * @return ClassroomTimeTable 表示该教室的从今天开始往后一定时间的占用情况
     */
    public ClassroomTimeTable getClassroomTimeTable(int classroomId){

        String name = classroomMapper.selectById(classroomId).getClassroomName();

        /*
         * 从数据库读取所选教师的时间表
         * 然后转化为officeHourTime字段
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

        // 转换timetable格式
        List<ClassroomEvent> classroomEvents = classroomEventMapper.selectByIdAndTime(classroomId,today,todayAfterDayLen);

        // 转化
        List<TimeTable> formatTimetable = formatTimetable(
                today,
                todayAfterDayLen,
                classroomEvents
                        .stream()
                        .map(classroomEvent -> new TableEvent(
                                classroomEvent.getAppointmentDate(),
                                classroomEvent.getStartTime(),
                                classroomEvent.getEndTime(),
                                classroomEvent.getState()
                        ))
                        .toList()
        );


        return new ClassroomTimeTable(name,specialTimes,formatTimetable);
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
    public void banTeacher(String teacherId, TeacherBanTime banRequest){
        // 获取教师的名字
        String name = userInfoMapper.selectById(teacherId).getUsername();

        OfficeHourEvent banTime = new OfficeHourEvent();
        banTime.setTeacher(name);
        banTime.setState(0);
        LocalDateTime startDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(banRequest.getStartDate()), ZoneId.systemDefault());
        LocalDateTime endDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(banRequest.getEndDate()), ZoneId.systemDefault());
        LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(banRequest.getStartTime()), ZoneId.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(banRequest.getEndTime()), ZoneId.systemDefault());
        LocalDateTime modifiedStart = startTime.withYear(startDate.getYear())
                .withMonth(startDate.getMonthValue())
                .withDayOfMonth(startDate.getDayOfMonth());
        LocalDateTime modifiedEnd = endTime.withYear(endDate.getYear())
                .withMonth(endDate.getMonthValue())
                .withDayOfMonth(endDate.getDayOfMonth());
        banTime.setStartTime(modifiedStart);
        banTime.setEndTime(modifiedEnd);
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        for (int i = 0; i <= daysBetween; i++) {
            //利用for循环添加禁用天数
            officeHourEventMapper.insertOfficeHourEvent(banTime);
        }
    }
}
