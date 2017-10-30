package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = "symbol")
@AllArgsConstructor
public class Player {
    private String id;
    private char symbol;

    public Player(String id){
        this.id = id;
    }

    @Override
    public String toString(){
        return "<@" + id + ">";
    }
}
