package com.example.demo.security.handler;

import com.example.demo.data.model.Result;
import com.example.demo.utils.ResponseOutput;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败
 */
public class AuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        Object body = new Result.Failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        ResponseOutput.output(httpServletResponse, body);
    }
}
