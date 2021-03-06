package com.example.demo.exceptions;

import com.example.demo.model.Game;
import lombok.Getter;


public class GameException extends RuntimeException {

    @Getter
    private Game game;

    public GameException(Game game, String msg){
        super(msg);
        this.game = game;
    }
}
