package com.example.demo.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseOutput {
    public static void output(HttpServletResponse response, Object result) throws IOException {
        response.setHeader("content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.println(JSONChange.objToJson(result));
        out.flush();
        out.close();
    }
}
