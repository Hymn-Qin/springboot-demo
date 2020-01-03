package com.example.demo.security.model;

import lombok.Data;

@Data
public class LoggedInUser {
    private String username;
    private String password;
    private Boolean rememberMe;
}
