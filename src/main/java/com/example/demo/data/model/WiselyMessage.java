package com.example.demo.data.model;

import lombok.Data;

/**
 * WiselyMessage实体，用于浏览器和服务端发送消息参数
 */
@Data
public class WiselyMessage {

    private String name;

    private String message;

    public WiselyMessage(String name, String message) {
        this.name = name;
        this.message = message;
    }

}
