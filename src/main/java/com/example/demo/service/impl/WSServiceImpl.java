package com.example.demo.service.impl;

import com.example.demo.service.WSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WSServiceImpl implements WSService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final AtomicInteger OnlineCount = new AtomicInteger(0);
    private static CopyOnWriteArraySet<Session> SessionSet = new CopyOnWriteArraySet<Session>();

    @Override
    public void addSession(String user, Session session) {
        SessionSet.add(session);
        int count = OnlineCount.incrementAndGet();//在线人数加1
        logger.info("当前连接数 count: {}", count);
    }

    @Override
    public void removeSession(String user, Session session) {
        SessionSet.remove(session);
        int count = OnlineCount.decrementAndGet();
        logger.info("当前连接数 count: {}", count);
    }

    /**
     * 发送消息，实践表明，每次浏览器刷新，session会发生变化。
     *
     * @param session
     * @param message
     */
    @Override
    public void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(
                    String.format("%s (From Server，Session ID=%s)", message, session.getId())
            );
        } catch (IOException e) {
            logger.error("消息发送失败: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 群发消息
     *
     * @param message
     * @throws IOException
     */
    @Override
    public void sendMessageForAll(String message) {
        for (Session session : SessionSet) {
            if (session.isOpen()) sendMessage(session, message);
        }
    }

    @Override
    public void sendError(Session session, Throwable throwable) {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throwable.printStackTrace();
    }
}
