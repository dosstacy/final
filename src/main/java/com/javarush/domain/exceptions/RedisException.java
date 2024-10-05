package com.javarush.domain.exceptions;

public class RedisException extends RuntimeException {
    public RedisException(String message) {
        super(message);
    }
}
