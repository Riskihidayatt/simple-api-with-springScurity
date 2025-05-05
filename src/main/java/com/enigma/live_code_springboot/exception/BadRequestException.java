package com.enigma.live_code_springboot.exception;
//untuk error validasi dan input
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
