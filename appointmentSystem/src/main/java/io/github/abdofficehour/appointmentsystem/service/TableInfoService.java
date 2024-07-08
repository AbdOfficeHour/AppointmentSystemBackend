package io.github.abdofficehour.appointmentsystem.service;

import io.github.abdofficehour.appointmentsystem.config.Properties;
import io.github.abdofficehour.appointmentsystem.mapper.*;
import io.github.abdofficehour.appointmentsystem.pojo.data.ClassroomEvent;
import io.github.abdofficehour.appointmentsystem.pojo.data.OfficeHourEvent;
import io.github.abdofficehour.appointmentsystem.pojo.data.TeacherTimeTable;
import io.github.abdofficehour.appointmentsystem.pojo.schema.classroomClassification.ClassroomClassificationSchema;
import io.github.abdofficehour.appointmentsystem.pojo.schema.classroomClassification.ClassroomsInClassification;
import io.github.abdofficehour.appointmentsystem.pojo.schema.teacherClassification.TeacherClassificationSchema;
import io.github.abdofficehour.appointmentsystem.pojo.schema.teacherClassification.TeachersInClassification;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

    /**
     * 获取officehour选择器
     * @return TeacherClassificationSchema列表 用于记录officeHour教师类型对应教师
     */
    public List<TeachersInClassification> getOfficeHourPicker(){
        LocalDate today = LocalDate.now();
        LocalDate dayAfterLen = today.plusDays(properties.getDateLen());

        // 读取所有的teacher相关的classification
        List<TeacherClassificationSchema> teacherClassificationSchemas = userInfoMapper.selectAllClassification(today,dayAfterLen);
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
                classrooms.put("classroomName", iterClassroom.getClassroomName());
                // 将classroom信息放入classroomClassificationSchema
                classroomsInClassification.setClassrooms(new ArrayList<>(){{add(classrooms);}});
                classroomsInClassificationMap.put(iterClassroom.getClassification(), classroomsInClassification);
            }else{
                classroomsInClassificationMap.get(iterClassroom.getClassification()).getClassrooms().add(new HashMap<>(){{
                    put("classroomId", iterClassroom.getClassroomId());
                    put("classroomName", iterClassroom.getClassroomName());
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
                    teacherTimeTable.getAppointmentDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                    teacherTimeTable.getStartTime().toInstant(ZoneOffset.UTC).toEpochMilli(),
                    teacherTimeTable.getEndTime().toInstant(ZoneOffset.UTC).toEpochMilli())
                ).toList();

        /*
        转换timeTable的格式，用于表示教师的空闲和非空闲时间
         */

        // 读取教师的officehour
        List<OfficeHourEvent> officeHourEvents = officeHourEventMapper.selectOfficeHourEventByTeacherIdAndForDayLen(teacherId, today, todayAfterDayLen);

        /*
        检查一下该教师是否还可预约
        如果是今天或者之后没有officeHour就不能预约
        否则就到最后一天officeHour为止
         */

        if(!specialTimes.isEmpty()){
            Instant instant = Instant.ofEpochMilli(specialTimes.get(specialTimes.size()-1).getDate()).atZone(ZoneOffset.UTC).toInstant();
            todayAfterDayLen = LocalDate.ofInstant(instant,ZoneOffset.UTC);
        }else{
            // 不应该有时间表的情况
            return new OfficeHourTimetable(name,specialTimes,null);
        }

        // 获取的最终timeTable格式
        List<TimeTable> formatTimetable = formatTimetable(
                today,
                todayAfterDayLen,
                officeHourEvents
                        .stream()
                        .map(officeHourEvent -> new TableEvent(
                                officeHourEvent.getAppointmentDate(),
                                officeHourEvent.getStartTime(),
                                officeHourEvent.getEndTime()
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
                                classroomTimeTable.getAppointmentDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                                classroomTimeTable.getStartTime().toInstant(ZoneOffset.UTC).toEpochMilli(),
                                classroomTimeTable.getEndTime().toInstant(ZoneOffset.UTC).toEpochMilli())
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
                                classroomEvent.getEndTime()
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
            Long eventDate = tableEvent.getAppointmentDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();


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
            thisDateTimetable.setDate(iterDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());

            // 查询对应日期的事件
            List<TableEvent> thisTableEvent = dateMapTableEvent.get(iterDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());

            // 不存在事件的情况
            if (Objects.isNull(thisTableEvent)){
                List<Period> busyTime = new ArrayList<>();
                List<Period> availableTime = new ArrayList<>(){{
                    add(new Period(startOfToday.toInstant(ZoneOffset.UTC).toEpochMilli(),startOfToday.toInstant(ZoneOffset.UTC).toEpochMilli()));
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
                            officeHourEvent.getStartTime().toInstant(ZoneOffset.UTC).toEpochMilli(),
                            officeHourEvent.getEndTime().toInstant(ZoneOffset.UTC).toEpochMilli()
                    ))
                    .toList();

            // 获取空闲时间，这就有一点小麻烦了
            List<Period> availableTime = new ArrayList<>();

            // 特殊情况，如果有busy time的话，就从officeHourTime开始
            Period firstAvailablePeriod = new Period();
            firstAvailablePeriod.setStart(startOfToday.toInstant(ZoneOffset.UTC).toEpochMilli());
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
            endAvailablePeriod.setEnd(endOfToday.toInstant(ZoneOffset.UTC).toEpochMilli());
            availableTime.add(endAvailablePeriod);

            thisDateTimetable.setBusy(busyTime);
            thisDateTimetable.setAvailable(availableTime);

            resultTimeTable.add(thisDateTimetable);
        }

        return resultTimeTable;
    }
}
