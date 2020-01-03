package com.example.demo.security.handler;

import com.example.demo.data.model.Result;
import com.example.demo.security.utils.LoggerAuth;
import com.example.demo.utils.ResponseOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 没有权限访问
 */
public class JWTAccessDeniedHandler implements AccessDeniedHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 当用户尝试访问需要权限才能的REST资源而权限不足的时候，
     * 将调用此方法发送401响应以及错误信息
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        accessDeniedException = new AccessDeniedException("Sorry you don not enough permissions to access it!");
//        response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());

        Object body = new Result.Failure(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
        LoggerAuth.logger(request, body);
        ResponseOutput.output(response, body);
    }
}
