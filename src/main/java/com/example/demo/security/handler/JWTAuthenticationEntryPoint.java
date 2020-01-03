package com.example.demo.security.handler;

import com.example.demo.data.model.Result;
import com.example.demo.security.utils.LoggerAuth;
import com.example.demo.utils.ResponseOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 未登录
 */
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 当用户尝试访问需要权限才能的REST资源而不提供Token或者Token错误或者过期时，
     * 将调用此方法发送401响应以及错误信息
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());

        Object body = new Result.Failure(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        LoggerAuth.logger(request, body);
        ResponseOutput.output(response, body);
    }
}
