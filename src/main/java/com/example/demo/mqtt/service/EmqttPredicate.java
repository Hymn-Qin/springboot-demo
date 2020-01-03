package com.example.demo.mqtt.service;

import com.example.demo.mqtt.data.model.MqttEventMessage;
import org.springframework.stereotype.Component;

@Component
public class EmqttPredicate {

    public Boolean test(MqttEventMessage event){
        //测试内容

        return Boolean.TRUE;
    }
}
