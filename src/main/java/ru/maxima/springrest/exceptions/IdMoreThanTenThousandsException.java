package ru.maxima.springrest.exceptions;

public class IdMoreThanTenThousandsException extends RuntimeException {
    public IdMoreThanTenThousandsException(String message) {
        super(message);
    }
}
