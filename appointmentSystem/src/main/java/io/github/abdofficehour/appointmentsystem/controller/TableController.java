package io.github.abdofficehour.appointmentsystem.controller;

import io.github.abdofficehour.appointmentsystem.pojo.schema.ResponseMap;
import io.github.abdofficehour.appointmentsystem.pojo.schema.TeacherClassificationSchema;
import io.github.abdofficehour.appointmentsystem.service.TableInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


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
        List<TeacherClassificationSchema> classificationSchemaList = tableInfoService.getOfficeHourPicker();

        return new ResponseMap(0,"获取成功",classificationSchemaList);
    }

    @Operation(summary = "检索教师时间表")
    @GetMapping("/officehour/{teacherId}")
    public ResponseMap getTeacherTimeTable(@PathVariable String teacherId){
        return null;
    }
}
