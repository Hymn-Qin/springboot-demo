package com.example.demo.controller;

import com.example.demo.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TestService service;

    @GetMapping("/")
    public String index() {
        return "Hymnal Welcome !";
    }

    @GetMapping("/uid")
    String uid(HttpSession session) {
        UUID uid = (UUID) session.getAttribute("uid");
        if (uid == null) {
            uid = UUID.randomUUID();
        }
        session.setAttribute("uid", uid);
        return session.getId();
    }

    @GetMapping("/async")
    public Object testAsync() {
        long start = System.currentTimeMillis();
        logger.info("异步 start");

        service.asyncMethod();

        long end = System.currentTimeMillis();
        logger.info("异步 end");

        logger.info("异步耗时: {}", end - start);
        return "";
    }

    @GetMapping("/asyncR")
    public Object testAsyncR() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        logger.info("异步 start");

        Future<String> stringFuture = service.asyncMethodR();
        String result = stringFuture.get();
        //Future的get方法为阻塞方法，只有当异步方法返回内容了，程序才会继续往下执行。
        // get还有一个get(long timeout, TimeUnit unit)重载方法，我们可以通过这个重载方法设置超时时间，
        // 即异步方法在设定时间内没有返回值的话，直接抛出java.util.concurrent.TimeoutException异常。
        //
        //比如设置超时时间为60秒：

        long end = System.currentTimeMillis();
        logger.info("异步 end: {}", result);

        logger.info("异步耗时: {}", end - start);
        return result;
    }

    @GetMapping("/sync")
    public Object testSync() {
        long start = System.currentTimeMillis();
        logger.info("同步 start");

        service.syncMethod();

        long end = System.currentTimeMillis();
        logger.info("同步 end");

        logger.info("同步耗时: {}", end - start);
        return "";
    }
}
