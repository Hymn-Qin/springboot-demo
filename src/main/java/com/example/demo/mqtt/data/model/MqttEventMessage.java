package com.example.demo.mqtt.data.model;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MqttEventMessage extends ApplicationEvent {

    private static final long serialVersionUID = 8716875367914505282L;
    /**
     * 主题
     */
    private String topic;
    /**
     * 发送的消息
     */
    private String message;

    public MqttEventMessage(Object source, String topic, String message) {
        super(source);
        this.topic = topic;
        this.message = message;
    }
}
