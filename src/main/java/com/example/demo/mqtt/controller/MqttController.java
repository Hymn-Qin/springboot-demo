package com.example.demo.mqtt.controller;

import com.example.demo.mqtt.service.IMqttSender;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * MQTT消息发送
 *
 * @author BBF
 */
@RestController
@RequestMapping(value = "/")
public class MqttController {

    /**
     * 注入发送MQTT的Bean
     */
    @Resource
    private IMqttSender iMqttSender;

    /**
     * 发送MQTT消息
     *
     * @param message 消息内容
     * @return 返回
     */
    @GetMapping(value = "/mqtt")
    public Object sendMqtt(@RequestParam(value = "msg") String message) {
        iMqttSender.sendToMqtt("topic1", 2, true, message);
        return "null";
    }
}
