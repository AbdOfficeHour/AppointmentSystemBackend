package io.github.abdofficehour.appointmentsystem.filter;

import io.github.abdofficehour.appointmentsystem.pojo.UserInfo;
import io.github.abdofficehour.appointmentsystem.service.RestRequestService;
import io.github.abdofficehour.appointmentsystem.service.UserInfoService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AuthFilter implements Filter {

    FilterConfig filterConfig;

    @Autowired
    private RestRequestService restRequestService;

    @Autowired
    private UserInfoService userInfoService;

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
        String id = restRequestService.AuthToken(token);

        // 根据用户id查询信息
        if(!"".equals(id)){
            UserInfo userInfo = userInfoService.searchUserById(id);
            if (!Objects.isNull(userInfo)){
                ifSuccess = true;
                httpRequest.setAttribute("userinfo",userInfo);
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
