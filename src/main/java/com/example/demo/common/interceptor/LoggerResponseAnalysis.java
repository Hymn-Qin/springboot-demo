package com.example.demo.common.interceptor;

import com.example.demo.data.model.Result;
import com.example.demo.utils.JSONChange;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class LoggerResponseAnalysis<T> implements ResponseBodyAdvice<T> {
    private Logger logger = LoggerFactory.getLogger("HTTP");

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @SneakyThrows
    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        String url = serverHttpRequest.getURI().toString();
        String method = serverHttpRequest.getMethod().toString();
        logger.info("请求响应, url: {}, method: {}, result: {}", url, method, o);
        if (o instanceof Result || o instanceof Resource) {
            return o;
        }
        //null 或者空的处理　返回json 空对象[]　
        Object body = new Result.Success("success", o);
        //如果直接返回string 或者数字 ，包装一下
        if (o instanceof String || o == null || "".equals(o)) {
            return JSONChange.objToJson(body);
        }
        return body;
    }
}
