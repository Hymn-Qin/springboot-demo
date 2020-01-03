package com.example.demo.controller;

import com.example.demo.data.model.WiselyMessage;
import com.example.demo.service.WSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * websocket 简易聊天
 *
 * @author oKong
 */
//由于是websocket 所以原本是@RestController的http形式
//直接替换成@ServerEndpoint即可，作用是一样的 就是指定一个地址
//表示定义一个websocket的Server端
@Component
@RestController
@ServerEndpoint(value = "/ws/{usernick}")
public class WSController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private WSService service;

    /**
     * 连接事件 加入注解
     *
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam(value = "usernick") String userNick, Session session) {
        String message = "有新游客[" + userNick + session.getId() + "]加入聊天室!";
        logger.info(message);
        service.addSession(userNick, session);
        //此时可向所有的在线通知 某某某登录了聊天室
//        service.sendMessageForAll(message);
    }

    @OnClose
    public void onClose(@PathParam(value = "usernick") String userNick, Session session) {
        String message = "游客[" + userNick + "]退出聊天室!";
        logger.info(message);
        service.removeSession(userNick, session);
        //此时可向所有的在线通知 某某某登录了聊天室
        service.sendMessageForAll(message);
    }

    @OnMessage
    public void OnMessage(@PathParam(value = "usernick") String userNick, String message) {
        //类似群发
        String info = "游客[" + userNick + "]：" + message;
        logger.info(info);
        service.sendMessageForAll(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("异常:", throwable);
        service.sendError(session, throwable);
    }

    /**
     * 广播
     */
    @MessageMapping("/we")
    @SendTo("/topic.getResponse")
    public Object say(WiselyMessage message) {

        return new WiselyMessage("123", "welcome");
    }
}
