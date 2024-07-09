package io.github.abdofficehour.appointmentsystem.controller;

import io.github.abdofficehour.appointmentsystem.pojo.ResponseMap;
import io.github.abdofficehour.appointmentsystem.pojo.data.ClassroomEvent;
import io.github.abdofficehour.appointmentsystem.pojo.data.OfficeHourEvent;
import io.github.abdofficehour.appointmentsystem.pojo.data.TeacherBanTime;
import io.github.abdofficehour.appointmentsystem.pojo.data.UserInfo;
import io.github.abdofficehour.appointmentsystem.service.AppointmentService;
import io.github.abdofficehour.appointmentsystem.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1.1/Appointment")
@Tag(name = "预约事件相关接口")
//-------------------ymz倾情编写，返工找我就行QAQ---------------------//
public class AppointmentController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private AppointmentService appointmentService;

    @Operation(summary = "返回用户自己几个月内的Officehour预约信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/list/officehour")
    public ResponseMap getOfficeHourEvents(@RequestParam("time") int time,HttpServletRequest request) {

        // 返回的数据
        List<OfficeHourEvent> eventList;
        List<String> partnerIdList;
        List<String> partnerList = new ArrayList<>();
        List<Map> resultList = new ArrayList<>();

        //去数据库找到这个人的id是
        UserInfo userInfo = (UserInfo) request.getAttribute("userinfo");
        String id = userInfo.getId();

        eventList = appointmentService.searchUserById(id,time);
        if (eventList == null) return new ResponseMap(0, "获取成功", eventList);
        Map<String,Object> map = new HashMap<>();
        for (OfficeHourEvent result : eventList){
            Instant instantDate = result.getAppointmentDate().atStartOfDay().toInstant(ZoneOffset.UTC);
            Instant instantStartTime = result.getStartTime().toInstant(ZoneOffset.UTC);
            Instant instantEndTime = result.getEndTime().toInstant(ZoneOffset.UTC);
            Map<String,Object> timePeriod = new HashMap<>();
            timePeriod.put("date",instantDate);
            timePeriod.put("startTime",instantStartTime);
            timePeriod.put("endTime",instantEndTime);

            partnerIdList = appointmentService.searchById(result.getId());
            for (String partner : partnerIdList){
                String partnerName = userInfoService.searchUserById(partner).getUsername();
                partnerList.add(partnerName);
            }

            map.put("id",result.getId());
            map.put("student_name",result.getStudent());
            map.put("teacher_name",result.getTeacher());
            map.put("time",timePeriod);
            map.put("note",result.getNote());
            map.put("question",result.getQuestion());
            map.put("present",partnerList);
            map.put("state",result.getState());
            map.put("refuse_result",result.getRefuseResult());
            map.put("work_summary",result.getWorkSummary());
            resultList.add(map);
        }
        return new ResponseMap(0, "获取成功", resultList);
    }

    @Operation(summary = "更新事件信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "401", description = "权限错误"),
            @ApiResponse(responseCode = "102", description = "其他修改错误")
    })
    @PutMapping("/list/officehour/{id}")
    public ResponseMap updateOfficeHourEvent(@PathVariable int id, @RequestBody Map<String, Object> updateData, HttpServletRequest request) {
        try {
            String userId = ((UserInfo) request.getAttribute("userinfo")).getId();
            int result = appointmentService.updateOfficeHourEvent(id, updateData, userId);

            if (result == 0) {
                return new ResponseMap(0, "修改成功", null);
            } else if (result == 101) {
                return new ResponseMap(101, "权限错误", null);
            } else {
                return new ResponseMap(102, "其他修改错误", null);
            }
        } catch (Exception e) {
            return new ResponseMap(102, "其他修改错误", null);
        }
    }

    @Operation(summary = "获取OfficeHour可选老师")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/list/officehour/pickerList")
    public ResponseMap getAvailableTeachers() {
        List<Map<String, String>> teachers = appointmentService.getAvailableTeachers();
        Map<String, List<Map<String, String>>> data = new HashMap<>();
        data.put("teachers", teachers);
        return new ResponseMap(0, "获取成功", data);
    }

    @Operation(summary = "获取OfficeHour可选老师の可选时间段")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/list/officehour/pickerTime/{teacherId}")
    public ResponseMap getAvailableTimes(@PathVariable("teacherId") String teacherId) {
        List<Map<String, Object>> dateTimeList = appointmentService.getAppointmentsByTeacherId(teacherId);
        Map<String, List<Map<String, Object>>> data = new HashMap<>();
        data.put("dateTime", dateTimeList);
        return new ResponseMap(0, "获取成功", data);
    }

    @Operation(summary = "学生添加预约，state变为1")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @PostMapping("/list/officehour")
    public ResponseMap createAppointment(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        String teacher = (String) requestBody.get("teacher");
        Map<String, Object> time = (Map<String, Object>) requestBody.get("time");
        String note = (String) requestBody.get("note");
        String question = (String) requestBody.get("question");

        // 从 request 中获取学生信息
        String student = ((UserInfo) request.getAttribute("userinfo")).getId();

        boolean success = appointmentService.createAppointment(student, teacher, time, note, question);

        if (success) {
            return new ResponseMap(0, "预约成功", null);
        } else {
            return new ResponseMap(101, "预约失败", null);
        }
    }


//    下面是ClassRoom相关接口，方便后续简化代码注释

    @Operation(summary = "返回用户自己几个月内的Classroom预约信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/list/classroom")
    public ResponseMap getClassRoomEvents(@RequestParam("time") int time,HttpServletRequest request) {

        // 返回的数据
        List<ClassroomEvent> eventList;
        List<String> partnerIdList;
        List<String> partnerList = new ArrayList<>();
        List<Map> resultList = new ArrayList<>();

        //去数据库找到这个人的id是
        UserInfo userInfo = (UserInfo) request.getAttribute("userinfo");
        String id = userInfo.getId();

        eventList = appointmentService.searchClassRoomEventById(id,time);
        if (eventList == null) return new ResponseMap(0, "获取成功", eventList);
        Map<String,Object> map = new HashMap<>();
        for (ClassroomEvent result : eventList){
            Instant instantDate = result.getAppointmentDate().atStartOfDay().toInstant(ZoneOffset.UTC);
            Instant instantStartTime = result.getStartTime().toInstant(ZoneOffset.UTC);
            Instant instantEndTime = result.getEndTime().toInstant(ZoneOffset.UTC);
            Map<String,Object> timePeriod = new HashMap<>();
            timePeriod.put("date",instantDate);
            timePeriod.put("startTime",instantStartTime);
            timePeriod.put("endTime",instantEndTime);

            partnerIdList = appointmentService.searchClassroomById(result.getId());
            for (String partner : partnerIdList){
                String partnerName = userInfoService.searchUserById(partner).getUsername();
                partnerList.add(partnerName);
            }

            map.put("id",result.getId());
            map.put("applicant",result.getApplicant());
            map.put("classroom",result.getClassroom());
            map.put("time",timePeriod);
            map.put("isMedia",result.getIsMedia());
            map.put("isComputer",result.getIsComputer());
            map.put("isSound",result.getIsSound());
            map.put("present",partnerList);
            map.put("aim",result.getAim());
            map.put("events",result.getEvents());
            map.put("state",result.getState());
            resultList.add(map);
        }
        return new ResponseMap(0, "获取成功", resultList);
    }

    @Operation(summary = "更新Class事件信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "401", description = "权限错误"),
            @ApiResponse(responseCode = "102", description = "其他修改错误")
    })
    @PutMapping("/list/classroom/{id}")
    public ResponseMap updateClassroomEvent(@PathVariable int id, @RequestBody Map<String, Object> updateData, HttpServletRequest request) {
        try {
            String userId = ((UserInfo) request.getAttribute("userinfo")).getId();
            int result = appointmentService.updateClassroomEvent(id,updateData,userId);

            if (result == 0) {
                return new ResponseMap(0, "修改成功", null);
            } else if (result == 101) {
                return new ResponseMap(101, "权限错误", null);
            } else {
                return new ResponseMap(102, "其他修改错误", null);
            }
        } catch (Exception e) {
            return new ResponseMap(102, "其他修改错误", null);
        }
    }

    @Operation(summary = "获取当前可选Classroom")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/list/classroom/pickerList")
    public ResponseMap getAvailableClassrooms() {
        List<Map<String, String>> classrooms = appointmentService.getAvailableClassrooms();
        Map<String, List<Map<String, String>>> data = new HashMap<>();
        data.put("classrooms", classrooms);
        return new ResponseMap(0, "获取成功", data);
    }

    @Operation(summary = "获取可选Classroomの可选时间段")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/list/classroom/pickerTime/{classroom}")
    public ResponseMap getClassroomAvailableTimes(@PathVariable("classroom") String classroom) {
        List<Map<String, Object>> dateTimeList = appointmentService.getAppointmentsByClassroomId(classroom);
        Map<String, List<Map<String, Object>>> data = new HashMap<>();
        data.put("dateTime", dateTimeList);
        return new ResponseMap(0, "获取成功", data);
    }

    @Operation(summary = "用户添加Classroom预约，state变为1")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @PostMapping("/list/classroom")
    public ResponseMap createClassroomAppointment(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        String classroom = (String) requestBody.get("classroom");
        Map<String, Object> time = (Map<String, Object>) requestBody.get("time");
        boolean isMedia = (Boolean) requestBody.get("isMedia");
        boolean isComputer = (Boolean) requestBody.get("isComputer");
        boolean isSound = (Boolean) requestBody.get("isSound");
        String present = requestBody.get("present").toString();
        String aim = (String) requestBody.get("aim");
        String events = (String) requestBody.get("events");
        int state = (Integer) requestBody.get("state");

        boolean success = appointmentService.createClassroomEvent(classroom, time, isMedia, isComputer, isSound, present, aim, events, state);
        if (success) {
            return new ResponseMap(0, "预约成功", null);
        } else {
            return new ResponseMap(101, "预约失败", null);
        }
    }
}
