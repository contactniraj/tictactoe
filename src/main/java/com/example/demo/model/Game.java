package com.example.demo.model;

import com.example.demo.exceptions.GameException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(exclude={"board", "gameSteps"})
public class Game {
    @Getter
    @Setter
    private String id;

    @Getter
    private char[][] board = new char[3][3];

    @Getter
    private Player player1;
    @Getter
    private Player player2;

    @Getter
    private Player winner;

    private List<GameStep> gameSteps;

    public Game(String channelId, String player1, String player2){
        this.id = channelId;
        this.player1 = new Player(player1);
        this.player2 = new Player(player2);

        char count = '1';
        for(int i=0; i<board.length; i++){
            for(int j=0;j<board.length;j++){
                board[i][j] = count++;
            }
        }
    }

    public Response start(){
        this.player1.setSymbol('X');
        this.player2.setSymbol('O');
        this.gameSteps = new ArrayList<>();

        return new Response(null, board, "", player1, player2);
    }

    public Response start(String id, Player p1, Player p2){
        this.player1 = p1;
        this.player1.setSymbol('X');
        this.player2 = p2;
        this.player2.setSymbol('O');
        this.gameSteps = new ArrayList<>();

        return new Response(null, board, "", player1, player2);
    }

    public Response play(Player player, int x, int y){

        validateUser(player);
        validateCell(x,y);

        gameSteps.add(new GameStep(player, x, y));
        board[x][y] = player.getSymbol();

        doPlayerWon(player, x, y);

        Player nextPlayer = getNextPlayer(player);
        if(this.winner != null){
            return new Response(winner, board,
                    "", player,
                    nextPlayer,
                    "Congratulations " + winner.toString() +", you won!! Play again soon");
        }
        return new Response(null,
                board,
                "",
                player,
                nextPlayer,
                "Game on, Take your turn " + nextPlayer.toString());
    }

    public void doPlayerWon(Player player, int x, int y) {
        char symbol = player.getSymbol();
        boolean hasWon = (board[x][0] == symbol && symbol == board[x][1] && symbol == board[x][2])
                || (board[0][y] == symbol && board[1][y] == symbol && symbol == board[2][y])
                || (x == y && board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol)
                || (x + y == 2 && board[0][2] == symbol && symbol == board[1][1] && board[2][0] == symbol);
        if(hasWon) {
            winner = player;
        };
    }

    private void validateUser(Player p){
        if(!(p.equals(player1) || p.equals(player2))) {
            throw new GameException(this, p.getId() + " cannot participate in this game");
        }

        if(gameSteps.size() > 0 && p.equals(gameSteps.get(gameSteps.size()-1).getPlayer())){
            throw new GameException(this, "Wait!! go slow. Next play belongs to other user " + getNextPlayer(p).toString());
        }
    }

    private void validateCell(int x, int y){
        if(!( (0 <= x && x < board.length)  ||  (0 <= y && y < board[0].length))){
            throw new GameException(this, "Please know your boundries[1-9] for game :D ");
        }
        if(board[x][y] == 'X' || board[x][y] == 'O'){
            throw new GameException(this, "Please choose unused slot");
        }
    }

    public Response view(String channel){

        Player winner = getWinner();
        Player nextPlayer = gameSteps.size() > 0?getNextPlayer(gameSteps.get(gameSteps.size()-1).getPlayer()):player1;

            if(winner != null){
            return new Response(winner, board, "",
                    gameSteps.get(gameSteps.size()-1).getPlayer(), nextPlayer,
                    "Congratulations " + winner.toString() +", you won!! Play again soon");
        }
        return new Response(null, board,"",
                gameSteps.get(gameSteps.size()-1).getPlayer(), nextPlayer,
                "Game on," + nextPlayer.toString() + " takes the next turn" );

    }

    public boolean isFinished(){
        return true;
    }

    public boolean isRowUnique(char[] row, char ch){
        boolean isUnique = true;
        for(char c:row){
            if(c != ch){
                return false;
            }
        }
        return isUnique;
    }

    public boolean isColumnUnique(int column, char ch){
        boolean isUnique = true;
        for(int i=0; i<board[0].length;i++ ){
            if(board[i][column] != ch){
                return false;
            }
        }
        return isUnique;
    }

    public boolean isDiagonalUniqe(char ch){
        boolean isUnique = true;
        for(int i=0, j=0; i<board.length; i++,j++){
            if(board[i][j] != ch){
                return false;
            }
        }

        for(int i=0, j=board.length-1; i<board.length; i++, j--){
            if(board[i][j] != ch){
                return false;
            }
        }

        return isUnique;
    }

    public Player getPlayerById(String id){
        if(player1.getId().equals(id)){
            return player1;
        }else if(player2.getId().equals(id)){
            return player2;
        }

        return null;
    }
    public Player getNextPlayer(Player currentPlayer){
        if(player1.getId().equals(currentPlayer.getId())){
            return player2;
        }else{
            return player1;
        }
    }

    public boolean isGameDone(){
        return gameSteps.size() == 9;
    }
}
