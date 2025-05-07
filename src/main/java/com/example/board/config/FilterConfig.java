package com.example.board.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.board.filter.LoginFilter;

import jakarta.servlet.Filter;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<Filter> filterBean() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(new LoginFilter());
        bean.addUrlPatterns("/signin", "/board/write");
        return bean;
    }
}
