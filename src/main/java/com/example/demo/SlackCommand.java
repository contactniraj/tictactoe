package com.example.demo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SlackCommand {

    public String token;
    public String team_id;
    public String team_domain;
    public String channel_id;
    public String channel_name;
    public String user_id;
    public String user_name;
    public String command;
    public String text;
    public String response_url;
    public String trigger_id;
}
