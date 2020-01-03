package com.example.demo.exception;

/**
 * @author shuang.kou
 */
public class UsernameAlreadyExistException extends RuntimeException {
    private static final long serialVersionUID = -474199639981357810L;

    public UsernameAlreadyExistException() {
    }

    public UsernameAlreadyExistException(String message) {
        super(message);
    }
}
