package com.hsu.shimpyoo.domain.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDto {
    private List<ChatChoice> choices;

    @Getter
    @Setter
    public static class ChatChoice {
        private int index;
        private ChatMessageDto message;
    }
}