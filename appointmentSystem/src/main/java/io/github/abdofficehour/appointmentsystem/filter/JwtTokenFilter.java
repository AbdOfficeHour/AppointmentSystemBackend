package io.github.abdofficehour.appointmentsystem.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JwtTokenFilter extends OncePerRequestFilter {

    private Set<String> excludedUrls;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = "";
        Cookie[] cookies = request.getCookies();

        if(cookies != null){
            for (Cookie cookie:cookies){
                String name = cookie.getName();
                String value = cookie.getValue();

                if(name.equals("jwt_token")){
                    token = value;
                    break;
                }
            }
        }

        if(!token.equals("")){
            request.setAttribute("token",token);
            filterChain.doFilter(request,response);
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"未登录");
        }
    }
}
