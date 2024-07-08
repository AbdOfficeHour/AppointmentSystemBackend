package io.github.abdofficehour.appointmentsystem.config;

import io.github.abdofficehour.appointmentsystem.filter.CreditHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class HandlerConfig implements WebMvcConfigurer {

    @Autowired
    private CreditHandlerInterceptor creditHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(creditHandlerInterceptor);
    }
}
