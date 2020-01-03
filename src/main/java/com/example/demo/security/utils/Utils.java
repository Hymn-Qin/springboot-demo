package com.example.demo.security.utils;

import com.example.demo.data.model.Result;

public class Utils {

    public static Object exceptionResponse(int code, String message) {
        return new Result.Failure(code, message);
    }
}
