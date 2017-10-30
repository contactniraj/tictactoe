package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class GameResponse {
    @Getter
    @Setter
    String response_type;
    @Getter
    @Setter
    String text;
    @Getter
    @Setter
    List<Attachment> attachments;
}
