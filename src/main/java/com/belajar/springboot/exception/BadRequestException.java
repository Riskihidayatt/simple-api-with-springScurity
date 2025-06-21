package com.belajar.springboot.exception;
//untuk error validasi dan input
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
