package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class VisitorController {

    @RequestMapping("/id")
    public String index(HttpServletRequest request) {
        String ip = request.getRemoteAddr();

        return "";
    }
}
