package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Response {
    Player winner;
    char[][] board;
    String error;
    Player currentPlayer;
    Player nextPlayer;
    String message;
    public Response(Player winner, char[][] board, String error){
        this.winner = winner;
        this.board = board;
        this.error = error;
    }
    public Response(Player winner, char[][] board){
        this.winner = winner;
        this.board = board;
    }
    public Response(Player winner, char[][] board, String error, Player currentPlayer, Player nextPlayer){
        this.winner = winner;
        this.board = board;
        this.error = error;
        this.currentPlayer = currentPlayer;
        this.nextPlayer = nextPlayer;
    }
}
