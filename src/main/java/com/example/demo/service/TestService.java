package com.example.demo.service;

import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.Future;

public interface TestService {

    @Async("asyncThreadPoolTaskExecutor")
    public void asyncMethod();

    @Async("asyncThreadPoolTaskExecutor")
    public Future<String> asyncMethodR();

    public void syncMethod();
}
