package com.example.demo.controller;

import com.example.demo.data.model.Events;
import com.example.demo.data.model.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/state")
public class StateMachineController {

    @Resource
    private StateMachine<States, Events> stateMachine;

    @GetMapping("/start")
    public Object start() {
        stateMachine.start();
        //支付事件
        stateMachine.sendEvent(Events.PAY);
        //收货事件
        stateMachine.sendEvent(Events.RECEIVE);
        return "null";
    }
}
