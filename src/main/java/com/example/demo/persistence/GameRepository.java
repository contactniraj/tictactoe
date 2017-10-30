package com.example.demo.persistence;

import com.example.demo.model.Game;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GameRepository {

    private Map<String, Game> gameMap = new HashMap<String, Game>();

    public void saveGame(Game game){
        gameMap.put(game.getId(), game);
    }

    public Game getGame(String channel){
        return gameMap.get(channel);
    }

    public void cancel(String channel){
        gameMap.remove(channel);
    }
}
