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

        // 读取教师的officehour
        List<OfficeHourEvent> officeHourEvents = officeHourEventMapper.selectOfficeHourEventByTeacherIdAndFor14Days(teacherId, today, todayAfter14Days);
        // 创建timeTable对象用于表示繁忙和可用时间段
        Map<Long,TimeTable> timeTableMap = new HashMap<>();
        // 将officeHourEvents转换为TimeTable
        for(OfficeHourEvent officeHourEvent : officeHourEvents){
            if(timeTableMap.containsKey(officeHourEvent.getAppointmentDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())){
                // 如果timeTableMap中没有这个时间段，就新建一个
                TimeTable timeTable = new TimeTable();
                timeTable.setDate(officeHourEvent.getAppointmentDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());


                // 为当前时间表创建时间段
                Period thisOfficeHourEventPeriod = new Period();
                thisOfficeHourEventPeriod.setStart(officeHourEvent.getStartTime().toInstant(ZoneOffset.UTC).toEpochMilli());
                thisOfficeHourEventPeriod.setEnd(officeHourEvent.getEndTime().toInstant(ZoneOffset.UTC).toEpochMilli());


            }else{
                // 如果有就添加
            }
        }


        return null;
    }
}
