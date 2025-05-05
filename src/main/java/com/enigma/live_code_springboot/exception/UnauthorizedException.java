package com.enigma.live_code_springboot.exception;
//untuk error akses tidak sah
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
