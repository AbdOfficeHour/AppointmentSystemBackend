package io.github.abdofficehour.appointmentsystem.controller;

import io.github.abdofficehour.appointmentsystem.pojo.ResponseMap;
import io.github.abdofficehour.appointmentsystem.pojo.data.TeacherBanTime;
import io.github.abdofficehour.appointmentsystem.pojo.schema.classroomClassification.ClassroomsInClassification;
import io.github.abdofficehour.appointmentsystem.pojo.schema.teacherClassification.TeachersInClassification;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.ClassroomTimeTable;
import io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable.OfficeHourTimetable;
import io.github.abdofficehour.appointmentsystem.service.TableInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        OfficeHourTimetable officeHourTimetable = tableInfoService.getTeacherTimeTable(teacherId);

        return new ResponseMap(0,"获取成功",officeHourTimetable);
    }

    @Operation(summary = "获取教室预约选择器")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/picker/classroom")
    public ResponseMap getClassroomPicker(){
        // todo 增加一下权限的判断

        List<ClassroomsInClassification> classificationResult =  tableInfoService.getClassroomPicker();
        Map<String,Object> response = new HashMap<>(){{
            put("pickerList",classificationResult);
        }};
        return new ResponseMap(0,"获取成功",response);
    }

    @Operation(summary = "获取教室时间表")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/classroom/{classroom}")
    public ResponseMap getClassroomTimeTable(@PathVariable int classroom){
        ClassroomTimeTable classroomTimeTable = tableInfoService.getClassroomTimeTable(classroom);

        return new ResponseMap(0,"获取成功",classroomTimeTable);
    }

    //from ymz (随时返工QAQ）
    @Operation(summary = "教师禁用时间段")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @PostMapping("/ban/{teacherId}")
    public ResponseMap banTimePeriod(@PathVariable("teacherId") String teacherId, @RequestBody TeacherBanTime banTimeRequest){
        tableInfoService.banTeacher(teacherId, banTimeRequest);

        return new ResponseMap(0,"获取成功",null);
    }
}
