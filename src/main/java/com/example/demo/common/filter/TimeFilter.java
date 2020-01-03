package com.example.demo.common.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 配置过滤器
 * 1. 注解
 * @Component 注解让TimeFilter成为Spring上下文中的一个Bean，
 * @WebFilter 注解的urlPatterns属性配置了哪些请求可以进入该过滤器，/*表示所有请求。
 *
 * 2. webconfig 中配置
 */
//@Component
//@WebFilter(urlPatterns = {"/*"})
public class TimeFilter implements Filter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("初始化过滤器");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("执行过滤器");
    }

    @Override
    public void destroy() {
        logger.info("销毁过滤器");
    }
}
