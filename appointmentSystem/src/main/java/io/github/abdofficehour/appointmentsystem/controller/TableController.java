package io.github.abdofficehour.appointmentsystem.controller;

import io.github.abdofficehour.appointmentsystem.pojo.ResponseMap;
import io.github.abdofficehour.appointmentsystem.pojo.data.ClassroomBanTime;
import io.github.abdofficehour.appointmentsystem.pojo.data.TeacherBanTime;
import io.github.abdofficehour.appointmentsystem.pojo.data.UserInfo;
import io.github.abdofficehour.appointmentsystem.pojo.schema.classroomClassification.ClassroomsInClassification;
import io.github.abdofficehour.appointmentsystem.pojo.schema.teacherClassification.TeachersInClassification;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.ClassroomTimeTable;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.OfficeHourTimetable;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.SpecialTime;
import io.github.abdofficehour.appointmentsystem.service.TableInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1.1/TableInfo")
public class TableController {

    @Autowired
    private TableInfoService tableInfoService;

    @Operation(summary = "获取officehour选择器")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/picker/officehour")
    public ResponseMap getOfficeHourPicker(){
        List<TeachersInClassification> classificationSchemaList = tableInfoService.getOfficeHourPicker();
        Map<String,Object> response = new HashMap<>(){{
            put("pickerList",classificationSchemaList);
        }};


        return new ResponseMap(0,"获取成功",response);
    }

    @Operation(summary = "检索教师时间表")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/officehour/{teacherId}")
    public ResponseMap getTeacherTimeTable(@PathVariable String teacherId){
        try {
            OfficeHourTimetable officeHourTimetable = tableInfoService.getTeacherTimeTable(teacherId);
            return new ResponseMap(0,"获取成功",officeHourTimetable);
        }catch(Exception ignored){
            return new ResponseMap(1,"失败",null);
        }
    }

    @Operation(summary = "获取教室预约选择器")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/picker/classroom")
    public ResponseMap getClassroomPicker(HttpServletRequest request){

        // todo 增加一下权限的判断
        HashMap<String, List<String>> userAuth = (HashMap<String, List<String>>) request.getAttribute("userAuth");
        if (userAuth != null) {
            // 获取 credit 列表
            List<String> credits = userAuth.get("credit");

            // 检查 credits 列表是否包含特定的权限
            if (credits != null && credits.contains("classroom:appointment:104")) {
                System.out.println("用户具有 classroom:appointment:104 权限");
                List<ClassroomsInClassification> classificationResult =  tableInfoService.getClassroomPicker();
                Map<String,Object> response = new HashMap<>(){{
                    put("pickerList",classificationResult);
                }};
                return new ResponseMap(0,"获取成功",response);
            } else if (credits != null && credits.contains("classroom:appointment:106")){
                System.out.println("用户具有 classroom:appointment:106 权限");
                List<ClassroomsInClassification> classificationResult =  tableInfoService.getClassroomPicker();
                Map<String,Object> response = new HashMap<>(){{
                    put("pickerList",classificationResult);
                }};
                return new ResponseMap(0,"获取成功",response);
            } else if (credits != null && credits.contains("classroom:appointment:202B")){
                System.out.println("用户具有 classroom:appointment:202B 权限");
                List<ClassroomsInClassification> classificationResult =  tableInfoService.getClassroomPicker();
                Map<String,Object> response = new HashMap<>(){{
                    put("pickerList",classificationResult);
                }};
                return new ResponseMap(0,"获取成功",response);
            } else {
                System.out.println("userAuth 属性不存在");
                return new ResponseMap(1, "没有权限", null);
            }
        } else {
            System.out.println("userAuth 属性不存在");
            return new ResponseMap(1,"没有用户属性",null);
        }
    }

    @Operation(summary = "获取教室时间表")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/classroom/{classroom}")
    public ResponseMap getClassroomTimeTable(@PathVariable int classroom){
        try{
            ClassroomTimeTable classroomTimeTable = tableInfoService.getClassroomTimeTable(classroom);
            return new ResponseMap(0,"获取成功",classroomTimeTable);
        } catch (Exception ignored) {
            return new ResponseMap(1,"获取失败",null);
        }

    }

    //from ymz (随时返工QAQ）
    @Operation(summary = "教师禁用时间段")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @PostMapping("/ban{teacherId}")
    public ResponseMap banTimePeriod(@PathVariable("teacherId") String teacherId, @RequestBody TeacherBanTime banTimeRequest){
        try {
            // 将 startDate 和 endDate 从时间戳转换为 LocalDate
            LocalDateTime startDateTime = Instant.ofEpochSecond(banTimeRequest.getStartDate())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            LocalDateTime endDateTime = Instant.ofEpochSecond(banTimeRequest.getEndDate())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // 将 startTime 和 endTime 从时间戳转换为 LocalDateTime
            LocalDateTime startTime = Instant.ofEpochSecond(banTimeRequest.getStartTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            LocalDateTime endTime = Instant.ofEpochSecond(banTimeRequest.getEndTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            List<Map<String, Object>> specialTime = tableInfoService.banTeacher(teacherId, startDateTime, endDateTime, startTime, endTime);
            if (specialTime != null) {
                Map<String, Object> specialTimeObject = Map.of("conflict_period", specialTime);
                return new ResponseMap(0, "时间冲突", specialTimeObject);
            }
            else {
                Map<String,Object> specialTimeObject = Map.of("conflict_period",null);
                return new ResponseMap(101,"禁用成功",specialTimeObject);
            }
        } catch (Exception e) {
            return new ResponseMap(102,"禁用失败",e.getMessage());
        }
    }

    @Operation(summary = "教室禁用时间段")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @PostMapping("/banclass/{classroomId}")
    public ResponseMap banClassroomTimePeriod(@PathVariable("classroomId") int classroomId, @RequestBody ClassroomBanTime banTimeRequest, HttpServletRequest request){
        try {
            UserInfo userInfo = (UserInfo) request.getAttribute("userinfo");
//            String userId = userInfo.getId();

            // 将 startDate 和 endDate 从时间戳转换为 LocalDate
            LocalDateTime startDateTime = Instant.ofEpochSecond(banTimeRequest.getStartDate())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            LocalDateTime endDateTime = Instant.ofEpochSecond(banTimeRequest.getEndDate())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // 将 startTime 和 endTime 从时间戳转换为 LocalDateTime
            LocalDateTime startTime = Instant.ofEpochSecond(banTimeRequest.getStartTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            LocalDateTime endTime = Instant.ofEpochSecond(banTimeRequest.getEndTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            List<Map<String, Object>> specialTime = tableInfoService.banClassroom(classroomId, startDateTime, endDateTime, startTime, endTime, userInfo.getId());
            if (specialTime != null) {
                Map<String, Object> specialTimeObject = Map.of("conflict_period", specialTime);
                return new ResponseMap(0, "时间冲突", specialTimeObject);
            }
            else {
                Map<String,Object> specialTimeObject = Map.of("conflict_period",null);
                return new ResponseMap(101,"禁用成功",specialTimeObject);
            }
        } catch (Exception e) {
            return new ResponseMap(102,"禁用失败",e.getMessage());
        }
    }
}
