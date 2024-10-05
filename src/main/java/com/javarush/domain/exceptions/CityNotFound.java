package com.javarush.domain.exceptions;

public class CityNotFound extends RuntimeException {
    public CityNotFound(String message) {
        super(message);
    }
}
