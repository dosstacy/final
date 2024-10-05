package com.javarush.domain.exceptions;

public class CountryException extends RuntimeException {
    public CountryNotFound(String message) {
        super(message);
    }
}
