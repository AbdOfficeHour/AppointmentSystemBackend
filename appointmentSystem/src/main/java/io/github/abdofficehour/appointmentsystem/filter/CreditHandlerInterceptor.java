package io.github.abdofficehour.appointmentsystem.filter;

import io.github.abdofficehour.appointmentsystem.annotation.Authority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

@Component
public class CreditHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HashMap<String, List<String>> userAuth = (HashMap<String, List<String>>) request.getAttribute("userAuth");

        if(handler instanceof HandlerMethod handlerMethod){
            Method method = handlerMethod.getMethod();

            Authority authority = method.getAnnotation(Authority.class);
            if(authority != null){
                String value = authority.value();
                // 检查是否具有该权限
                for(String auth: userAuth.get("credit")){
                    if(value == auth){
                        return true;
                    }
                }

                // 进行无权限的处理
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
