package com.belajar.springboot.exception;
//untuk error logika bisnis yang tidak valid
public class BusinessLogicException extends RuntimeException {
    public BusinessLogicException(String message) {
        super(message);
    }
}
