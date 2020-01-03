package com.example.demo.exception;

public class UsernameNotFoundException extends org.springframework.security.core.userdetails.UsernameNotFoundException {

    private static final long serialVersionUID = 6077680512374096852L;

    public UsernameNotFoundException(String msg) {
        super(msg);
    }

    public UsernameNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}

