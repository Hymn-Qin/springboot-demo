package com.example.demo.data;

import com.example.demo.data.model.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;

/**
 * 注册状态迁移监听
 */
@WithStateMachine
public class StateMachineEvent {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @OnTransition(target = "UNPAID")
    public void create() {
        logger.info("订单创建，待支付");
    }

    @OnTransition(source = "UNPAID",target = "WAITING_FOR_RECEIVE")
    public void pay() {
        logger.info("订单完成支付，待收货");
    }

    @OnTransition(source = "WAITING_FOR_RECEIVE", target = "DONE")
    public void receive() {
        logger.info("订单完成收货，订单完成");
    }
}
