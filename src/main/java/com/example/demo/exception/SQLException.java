package com.example.demo.exception;

public class SQLException extends RuntimeException {

    private static final long serialVersionUID = -134532724131170212L;

    public SQLException(String msg) {
        super(msg);
    }

    public SQLException(String msg, Throwable t) {
        super(msg, t);
    }
}

