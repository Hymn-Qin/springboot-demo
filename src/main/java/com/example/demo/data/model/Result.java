package com.example.demo.data.model;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T result;

    protected Result(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }

    public static class Success<T> extends Result<T> {
        public Success() {
            super(HttpServletResponse.SC_OK, "success", null);
        }

        public Success(String message) {
            super(HttpServletResponse.SC_OK, message, null);
        }

        public Success(String message, T result) {
            super(HttpServletResponse.SC_OK, message, result);
        }
    }

    public static class Failure<T> extends Result<T> {
        public Failure() {
            super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "failure", null);
        }

        public Failure(String message) {
            super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message, null);
        }

        public Failure(int code, String message) {
            super(code, message, null);
        }

        public Failure(int code, String message, T result) {
            super(code, message, result);
        }

        public Failure(String message, T result) {
            super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message, result);
        }
    }
}
