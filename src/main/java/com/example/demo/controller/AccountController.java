package com.example.demo.controller;

import com.example.demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
public class AccountController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/account/register")
    public String register() {
        return "";
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    @Cacheable(value = "user-key")
    public User getUser(HttpServletRequest request, @PathVariable String id) {
        logger.info("user: {}", "");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = new User("aa@123.com", "aa", "aa", "aa");
        System.out.println("若下面没出现“无缓存的时候调用”字样且能打印出数据表示测试成功");
        return user;
    }
}
