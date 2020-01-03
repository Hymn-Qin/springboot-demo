package com.example.demo.exception;

public class FileNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 2857343219372230835L;

    public FileNotFoundException(String msg) {
        super(msg);
    }

    public FileNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}

