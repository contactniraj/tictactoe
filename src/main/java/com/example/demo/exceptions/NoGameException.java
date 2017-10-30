package com.example.demo.exceptions;

public class NoGameException extends RuntimeException {
    public NoGameException(String msg){
        super(msg);
    }
}
