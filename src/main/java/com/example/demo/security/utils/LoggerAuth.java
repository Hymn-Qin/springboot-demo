package com.example.demo.security.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class LoggerAuth {
    private static Logger logger = LoggerFactory.getLogger("AuthHttp");
    public static void logger(HttpServletRequest request, Object body) {
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        logger.info("请求参数, url: {}, method: {}", url, method);
        logger.info("请求响应, url: {}, method: {}, body: {}", url, method, body);
    }
}
