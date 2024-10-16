package io.github.abdofficehour.appointmentsystem.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.abdofficehour.appointmentsystem.pojo.data.UserInfo;
import io.github.abdofficehour.appointmentsystem.service.RestRequestService;
import io.github.abdofficehour.appointmentsystem.service.UserInfoService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RegExUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AuthFilter implements Filter {

    FilterConfig filterConfig;

    @Autowired
    private RestRequestService restRequestService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        boolean ifSuccess = false;

        // 请求主程序，获取用户信息
        String token = (String) httpRequest.getAttribute("token");
        Boolean verifyResult = restRequestService.AuthToken(token);

        // 根据用户id查询信息
        if(verifyResult){
            String[] tokenStringList = token.split("\\.");
            if (tokenStringList.length < 2){
                return;
            }

            JsonNode payload = objectMapper.readTree(Base64.getDecoder().decode(tokenStringList[1]));
            String id = payload.get("username").asText();

            UserInfo userInfo = userInfoService.searchUserById(id);
            HashMap<String, List<String>> userAuth = userInfoService.SearchAuthorityByUser(id);
            if (!Objects.isNull(userInfo)){
                ifSuccess = true;
                httpRequest.setAttribute("userinfo",userInfo);
                httpRequest.setAttribute("userAuth",userAuth);
            }
        }

        //如果失败则不放行
        if(!ifSuccess) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "用户信息查询异常");
            return;
        }

        // 放行
        filterChain.doFilter(servletRequest,servletResponse);


    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
