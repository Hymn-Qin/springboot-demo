package com.example.demo.interceptor;

import com.example.demo.data.model.Result;
import com.example.demo.utils.JSONChange;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

@Component
public class HttpInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = "";
        Map<String, String[]> params = request.getParameterMap();
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }
        queryString = queryString.equals("") ? null : queryString.substring(0, queryString.length() - 1);
        logger.info("请求参数, url: {}, method: {}, params: {}", url, method, queryString);

        if (uri.equals("/hello") || uri.equals("/")) {
            return super.preHandle(request, response, handler);
        }

        String userId = request.getParameter("userId");
        if (userId == null) {
            Result result = new Result(401, "参数错误", null);
            this.output(response, result);
            return false;
        }
        return super.preHandle(request, response, handler);
    }

    private void output(HttpServletResponse response, Result result) throws Exception {
        response.setHeader("content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.println(JSONChange.objToJson(result));
    }

}
