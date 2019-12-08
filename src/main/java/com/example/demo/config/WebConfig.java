package com.example.demo.config;

import com.example.demo.interceptor.HttpInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        //拦截器1. 定义过滤拦截的url名称，拦截所有请求url
        registry.addInterceptor(new HttpInterceptor())
                .addPathPatterns("/**");
        //拦截器2. 。。。
        super.addInterceptors(registry);
    }
}
