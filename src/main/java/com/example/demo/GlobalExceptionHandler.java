package com.example.demo;

import com.example.demo.exceptions.GameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GameException.class)
    protected ResponseEntity<Object> exception(GameException ex, WebRequest request) {

        Game game = ((GameException) ex).getGame();
        return new ResponseEntity<>(new Response(null, game.getBoard(), ex.getMessage()),
                null,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> exception(Exception ex, WebRequest request) {
        Response resp = new Response(null, null, ex.getMessage());
        return new ResponseEntity<>(resp, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
