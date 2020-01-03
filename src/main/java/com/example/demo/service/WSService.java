package com.example.demo.service;

import javax.websocket.Session;

public interface WSService {

    public void addSession(String user, Session session);

    public void removeSession(String user, Session session);

    public void sendMessage(Session session, String message);

    public void sendMessageForAll(String message);

    void sendError(Session session, Throwable throwable);
}
