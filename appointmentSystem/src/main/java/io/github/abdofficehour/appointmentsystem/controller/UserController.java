package io.github.abdofficehour.appointmentsystem.controller;

import io.github.abdofficehour.appointmentsystem.pojo.data.UserInfo;
import io.github.abdofficehour.appointmentsystem.pojo.ResponseMap;
import io.github.abdofficehour.appointmentsystem.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.1/User")
@Tag(name = "用户相关接口")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @Operation(summary = "返回用户信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/info")
    public ResponseMap getUserInfo(HttpServletRequest request){

        UserInfo userInfo = (UserInfo) request.getAttribute("userinfo");

        @SuppressWarnings("unchecked")
        HashMap<String, List<String>> userAuth = (HashMap<String, List<String>>) request.getAttribute("userAuth");
        Map<String,Object> map = new HashMap<>();
        map.put("userID",userInfo.getId());
        map.put("username",userInfo.getUsername());
        map.put("email",userInfo.getEmail());
        map.put("phone",userInfo.getPhone());
        map.put("userAuthority",userAuth);


        return new ResponseMap(0,"成功",map);
    }

    @Operation(summary = "搜索学生")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/search")
    public ResponseMap searchStudent(@Param("searchData") String searchData){
        try {
            List<UserInfo> userInfos = userInfoService.SearchUserBySearchData(searchData);
            List<HashMap<String,String>> userListAfterFilter = userInfos.stream()
                    .map(userInfo -> {
                        HashMap<String,String> map = new HashMap<>();
                        map.put("userID",userInfo.getId());
                        map.put("username",userInfo.getUsername());
                        return map;
                    })
                    .toList();
            return new ResponseMap(0,"成功",new HashMap<String,Object>(){{
                put("userList",userListAfterFilter);
            }});
        }catch(Exception ignored){
            return new ResponseMap(1,"失败",null);
        }
    }

}
