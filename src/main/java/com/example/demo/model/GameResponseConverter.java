package com.example.demo.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class GameResponseConverter {

    public static GameResponse convert(Response response) {
        GameResponse resp = new GameResponse("in_channel", getFormattedBoard(response.board), getAttachments(response));
        System.out.println(new Gson().toJson(resp, GameResponse.class));

        return resp;
    }

    public static String getFormattedBoard(char[][] board) {
        if(board != null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(System.getProperty("line.separator"));

            for (int i = 0; i < board.length; i++) {
                buffer.append(" | ");
                for (int j = 0; j < board.length; j++) {
                    buffer.append("`" + board[i][j] + "`" + " | ");
                }
                buffer.append(System.getProperty("line.separator"));
            }
            return buffer.toString();
        }else{
            return "";
        }
    }

    public static List<Attachment> getAttachments(Response response) {
        List<Attachment> attachments = new ArrayList<Attachment>();


        StringBuffer buffer = new StringBuffer();

        buffer.append(response.getMessage());

        attachments.add(new Attachment(buffer.toString()));

        return attachments;
    }
}
