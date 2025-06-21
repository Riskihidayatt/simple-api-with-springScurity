package com.belajar.springboot.exception;

//untuk error data tidak ditemukan
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

}
