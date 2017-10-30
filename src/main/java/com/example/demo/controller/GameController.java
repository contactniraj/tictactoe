package com.example.demo.controller;

import com.example.demo.*;
import com.example.demo.exceptions.GameException;
import com.example.demo.exceptions.NoGameException;
import com.example.demo.model.*;
import com.example.demo.persistence.GameRepository;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;


@Controller
public class GameController {

    Logger log = Logger.getLogger(GameController.class);

    @Autowired
    GameRepository gameRepository;

    RestTemplate restTemplate = new RestTemplate();
    Gson gson = new Gson();

    @ResponseBody
    @RequestMapping("/game")
    public String ticTacToe(@ModelAttribute SlackCommand slackCommand){
        String[] text = slackCommand.text.split(" ");
        String usage = "/ttt start @partner - to start game\n" +
                "/ttt play 3 - to play a step\n" +
                "/ttt stop - stop the game.\n" +
                "/ttt view - get latest stage";
        if(text.length == 0){
            return usage;
        }

        try {
            switch (text[0]) {
                case "start":
                    // todo: validate text[0]
                    if(text.length != 2){
                        throw new GameException(null, "try: /ttt play @username");
                    }
                    Response startResp = start(slackCommand.getChannel_id(),
                            slackCommand.getUser_name(), text[1].replaceAll("@", ""));
                    postMessage(slackCommand.getResponse_url(), GameResponseConverter.convert(startResp));
                    return "";
                case "play":
                    int[] position = validateText(text);
                    Response playResp = play(slackCommand.getChannel_id(), slackCommand.getUser_name(),
                            position[0], position[1]);
                    postMessage(slackCommand.getResponse_url(), GameResponseConverter.convert(playResp));
                    return "";

                case "stop":

                    Response stopResp = cancel(slackCommand, slackCommand.getChannel_id());
                    postMessage(slackCommand.getResponse_url(), GameResponseConverter.convert(stopResp));
                    return "";

                case "view":
                    Response viewResp = view(slackCommand.getChannel_id());
                    postMessage(slackCommand.getResponse_url(), GameResponseConverter.convert(viewResp));
                    return "";

                default:
                    return usage;
            }
        } catch (Exception e){
            log.error("", e);
            processError(e, slackCommand);
            return "";
        }
    }

    private int[] validateText(String[] text){
        if(text.length > 2){
            throw new GameException(null, "Invalid parameter, choose between 1-9 number shown in matrix");
        }
        try{
            int n = Integer.parseInt(text[1]);
            switch(n){
                case 1:return new int[]{0,0};
                case 2:return new int[]{0,1};
                case 3:return new int[]{0,2};
                case 4:return new int[]{1,0};
                case 5:return new int[]{1,1};
                case 6:return new int[]{1,2};
                case 7:return new int[]{2,0};
                case 8:return new int[]{2,1};
                case 9:return new int[]{2,2};
                default:throw new GameException(null, "Invalid parameter, choose between 1-9 number shown in matrix");

            }
//            int i = (n-1)/3;
//            int j = (n-1)%3;
//            return new int[]{i,j};

        } catch (Exception e){
            throw new GameException(null, "Invalid parameter, choose between 1-9 number shown in matrix");

        }
    }

    private void postMessage(String url, GameResponse gameResponse ){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = (new Gson()).toJson(gameResponse);
        HttpEntity<String> entity = new HttpEntity<String>(body, headers);
        restTemplate.postForEntity(url, entity, String.class);

    }

    private void processError(Exception e, SlackCommand slackCommand){
        if(e instanceof GameException) {
            Game game = ((GameException) e).getGame();
            Response errorResp = new Response(null,
                    (game == null)? null : game.getBoard(),
                    "",
                    null,
                    null,
                    e.getMessage());
            postMessage(slackCommand.getResponse_url(), GameResponseConverter.convert(errorResp));
        } else if (e instanceof NoGameException ) {
            Response errorResp = new Response(null,
                    null,
                    "",
                    null,
                    null,
                    e.getMessage());
            postMessage(slackCommand.getResponse_url(),  GameResponseConverter.convert(errorResp));
        } else {
            Response errorResp = new Response(null,
                    null,
                    "",
                    null,
                    null,
                    "Internal error.");
            postMessage(slackCommand.getResponse_url(),  GameResponseConverter.convert(errorResp));

        }
    }

    private Response start(String channel,
                           String player1,
                           String player2) throws Exception {

        if(null != gameRepository.getGame(channel)){
            throw new GameException(null, "There is already a game in-progress at this channel");
        }

        Game game = new Game(channel, player1, player2);
        Response response = game.start();
        gameRepository.saveGame(game);
        response.setMessage("New Game Started between " + game.getPlayer1().toString() + " & " + game.getPlayer2().toString());
        return response;
    }

    private Response play(@RequestParam(value="channel") String channel,
                                         @RequestParam(value="player") String player,
                                         @RequestParam(value="x") int x,
                                         @RequestParam(value="y") int y) {
        Game game =  null;
        game = gameRepository.getGame(channel);

        if (game != null) {
            Player gamePlayer = game.getPlayerById(player);
            if(gamePlayer == null){
                new Response(game.getWinner(), game.getBoard(), "", null, null, "Invalid User for the game in progress, wait for the game to complete and then use /ttt start @Partner to start a new game");
            }

            Response response = game.play(gamePlayer, x, y);

            gameRepository.saveGame(game);

            if(response.getWinner() != null) {
                // remove state from repo if there is a winner so users can start new game in channel.
                gameRepository.cancel(channel);
                return response;
            }

            if(game.isGameDone()){
                gameRepository.cancel(channel);
                response.setMessage("Game finished without a winner. Play again!!");
            }

            return response;
        }
        throw new NoGameException("No Game. You can start new game by using /ttt start @partner");
    }

    private Response cancel(SlackCommand slackCommand, String channel) {
        Game game = gameRepository.getGame(channel);
        if(game != null){
            Player askingPlayer = new Player(slackCommand.getUser_name());
            if(askingPlayer.equals(game.getPlayer1()) || askingPlayer.equals(game.getPlayer2())) {
                gameRepository.cancel(game.getId());
                Response response = new Response(null, game.getBoard());
                response.setMessage("Game cancelled, use /ttt start @Partner to play again");
                return response;
            } else {
                throw new GameException(game, "Ping, <@"+slackCommand.getUser_name()+">, ask "
                        + game.getPlayer2().toString()
                        + " or "
                        + game.getPlayer1().toString()
                        + " to stop the game.");
            }
        }
        return new Response(null, null, "", null, null, "No game to stop in this channel, use /ttt start @partner to start a new game");
    }

    private Response view(String channel) {
        Game game =  null;
        game = gameRepository.getGame(channel);

        if (game != null) {
            Response response = game.view(channel);
            //gamePersister.saveGame(game);

            return response;
        }
        throw new NoGameException("No Game. You can start new game by using /ttt start @partner");
    }

}
