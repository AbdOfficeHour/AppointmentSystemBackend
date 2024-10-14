package io.github.abdofficehour.appointmentsystem.config;

import io.github.abdofficehour.appointmentsystem.filter.AuthFilter;
import io.github.abdofficehour.appointmentsystem.filter.CorsFilter;
import io.github.abdofficehour.appointmentsystem.filter.JwtTokenFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean AuthFilterRegistration(){
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(authFilter());
        registration.addUrlPatterns("/api/*");
        registration.setOrder(3);
        return registration;
    }

    @Bean
    public FilterRegistrationBean JwtFilterRegistration(){
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(jwtFilter());
        registration.addUrlPatterns("/api/*");
        registration.setOrder(2);
        return registration;
    }

    @Bean
    public FilterRegistrationBean CorsFilterRegistration(){
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(corsFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public Filter authFilter(){
        return new AuthFilter();
    }

    @Bean
    public Filter jwtFilter(){
        return new JwtTokenFilter();
    }

    @Bean
    public Filter corsFilter() {return new CorsFilter();}
}
