package com.enigma.live_code_springboot.exception;

//untuk error data tidak ditemukan
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

}
