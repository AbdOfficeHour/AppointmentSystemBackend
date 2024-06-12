package io.github.abdofficehour.appointmentsystem.controller;

import io.github.abdofficehour.appointmentsystem.pojo.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.1/User")
@Tag(name = "用户相关接口")
public class UserController {

    @Operation(summary = "返回用户信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/info")
    public UserInfo getUserInfo(HttpServletRequest request){

        return (UserInfo) request.getAttribute("userinfo");
    }


}
