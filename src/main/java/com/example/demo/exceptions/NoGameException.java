package com.example.demo.exceptions;

import com.example.demo.Game;

public class NoGameException extends RuntimeException {
    public NoGameException(String msg){
        super(msg);
    }
}
