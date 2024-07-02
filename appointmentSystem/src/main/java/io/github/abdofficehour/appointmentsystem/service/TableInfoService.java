package io.github.abdofficehour.appointmentsystem.service;

import io.github.abdofficehour.appointmentsystem.mapper.OfficeHourEventMapper;
import io.github.abdofficehour.appointmentsystem.mapper.TeacherTimeTableMapper;
import io.github.abdofficehour.appointmentsystem.mapper.UserInfoMapper;
import io.github.abdofficehour.appointmentsystem.pojo.OfficeHourEvent;
import io.github.abdofficehour.appointmentsystem.pojo.TeacherTimeTable;
import io.github.abdofficehour.appointmentsystem.pojo.schema.ClassificationSchema;
import io.github.abdofficehour.appointmentsystem.pojo.schema.TeacherClassificationSchema;
import io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable.OfficeHourTime;
import io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable.OfficeHourTimetable;
import io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable.Period;
import io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable.TimeTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class TableInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private TeacherTimeTableMapper teacherTimeTableMapper;

    @Autowired
    private OfficeHourEventMapper officeHourEventMapper;

    /**
     * 获取officehour选择器
     * @return TeacherClassificationSchema列表 用于记录officeHour教师类型对应教师
     */
    public List<TeacherClassificationSchema> getOfficeHourPicker(){
        // 读取所有的teacher相关的classification
        List<ClassificationSchema> classificationSchemas = userInfoMapper.selectAllClassification();
        // 用于存放teacher的信息
        Map<String,TeacherClassificationSchema> teacherMap = new HashMap<>();
        for(ClassificationSchema classificationSchema : classificationSchemas){

            if(!teacherMap.containsKey(classificationSchema.getClassification())){
                // 如果teacherMap中没有这个classification，就新建一个TeacherClassificationSchema
                TeacherClassificationSchema teacherClassificationSchema = new TeacherClassificationSchema();
                teacherClassificationSchema.setClassification(classificationSchema.getClassification());
                // 读取teacher的信息
                Map<String,Object> teachers = new HashMap<>();
                teachers.put("teacherId",classificationSchema.getTeacherId());
                teachers.put("teacherName",classificationSchema.getTeacherName());
                // 将teacher信息放入TeacherClassificationSchema
                teacherClassificationSchema.setTeachers(new ArrayList<>(){{add(teachers);}});
                teacherMap.put(classificationSchema.getClassification(),teacherClassificationSchema);
            }else{
                // 如果teacherMap中有这个classification，就直接读取出来,并将teacher信息放入
                teacherMap.get(classificationSchema.getClassification()).getTeachers().add(new HashMap<>(){{
                    put("teacherId",classificationSchema.getTeacherId());
                    put("teacherName",classificationSchema.getTeacherName());
                }});
            }
        }
        return teacherMap.values().stream().toList();
    }

    /**
     * 获取classroom选择器
     * @return 还没做完 //todo 完成教师选择器
     */
    public List<Map<String,String>> getClassroomPicker(){

        return null;
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
         * 读取从今天开始往后14天
         * todo 可能要修改这个时间逻辑
         */
        // 开始时间
        LocalDateTime today = LocalDateTime.now();
        // 结束时间
        LocalDateTime todayAfter14Days = LocalDateTime.now().plusDays(14);
        // 读取教师的时间表
        List<TeacherTimeTable> teacherTimeTables = teacherTimeTableMapper.selectTeacherTimeTable(teacherId,today,todayAfter14Days);
        // 将TeacherTimeTable转换为OfficeHourTime
        List<OfficeHourTime> officeHourTimes =
                teacherTimeTables.stream().map(teacherTimeTable ->
                    new OfficeHourTime(
                    teacherTimeTable.getAppointmentDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                    teacherTimeTable.getStartTime().toInstant(ZoneOffset.UTC).toEpochMilli(),
                    teacherTimeTable.getEndTime().toInstant(ZoneOffset.UTC).toEpochMilli())
                ).toList();

        /*
        转换timeTable的格式，用于表示教师的空闲和非空闲时间
         */

        // 读取教师的officehour
        List<OfficeHourEvent> officeHourEvents = officeHourEventMapper.selectOfficeHourEventByTeacherIdAndFor14Days(teacherId, today, todayAfter14Days);

        // 获取的最终timeTable格式
        List<TimeTable> formatTimetable = formatTimetable(officeHourTimes,officeHourEvents);

        return new OfficeHourTimetable(name,officeHourTimes,formatTimetable);
    }

    /**
     * 用于格式化时间表，将预约记录和officeHour时间进行结合
     * @return TimeTable对象
     */
    public List<TimeTable> formatTimetable(List<OfficeHourTime> officeHourTimes, List<OfficeHourEvent> officeHourEvents){
        // 对officeHourEvent进行预处理
        // 使用map记录每一个日期对应的officeHourEvent
        Map<Long,List<OfficeHourEvent>> dateMapOfficeHourEvents = new HashMap<>();
        for (OfficeHourEvent officeHourEvent: officeHourEvents){
            Long eventDate = officeHourEvent.getAppointmentDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();


            if(dateMapOfficeHourEvents.containsKey(eventDate)){
                // 如果这个日期存在的话
                dateMapOfficeHourEvents.get(eventDate).add(officeHourEvent);
            }else{
                // 不存在则创建
                // 利用静态块进行list的初始化
                dateMapOfficeHourEvents.put(eventDate,new ArrayList<>() {{add(officeHourEvent);}});
            }
        }

        // 最后返回的结果
        List<TimeTable> resultTimeTable = new ArrayList<>();

        // 遍历所有的officeHourTimes
        for (OfficeHourTime officeHourTime : officeHourTimes){
            TimeTable thisDateTimetable = new TimeTable();
            // 设置日期
            thisDateTimetable.setDate(officeHourTime.getDate());

            // 查询对应日期的事件
            List<OfficeHourEvent> thisOfficeHourEvents = dateMapOfficeHourEvents.get(officeHourTime.getDate());

            // 不存在事件的情况
            if (Objects.isNull(thisOfficeHourEvents)){
                List<Period> busyTime = new ArrayList<>();
                List<Period> availableTime = new ArrayList<>(){{
                    add(new Period(officeHourTime.getStart_time(),officeHourTime.getEnd_time()));
                }};

                thisDateTimetable.setBusy(busyTime);
                thisDateTimetable.setAvailable(availableTime);
                resultTimeTable.add(thisDateTimetable);
                continue;
            }

            // 先排个序
            thisOfficeHourEvents.sort(Comparator.comparing(OfficeHourEvent::getStartTime));

            // 获取繁忙时间
            List<Period> busyTime = thisOfficeHourEvents.stream()
                    .map(officeHourEvent -> new Period(
                            officeHourEvent.getStartTime().toInstant(ZoneOffset.UTC).toEpochMilli(),
                            officeHourEvent.getEndTime().toInstant(ZoneOffset.UTC).toEpochMilli()
                    ))
                    .toList();

            // 获取空闲时间，这就有一点小麻烦了
            List<Period> availableTime = new ArrayList<>();

            // 特殊情况，如果有busy time的话，就从officeHourTime开始
            Period firstAvailablePeriod = new Period();
            firstAvailablePeriod.setStart(officeHourTime.getStart_time());
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
            endAvailablePeriod.setEnd(officeHourTime.getEnd_time());
            availableTime.add(endAvailablePeriod);

            thisDateTimetable.setBusy(busyTime);
            thisDateTimetable.setAvailable(availableTime);

            resultTimeTable.add(thisDateTimetable);
        }

        return resultTimeTable;
    }
}
