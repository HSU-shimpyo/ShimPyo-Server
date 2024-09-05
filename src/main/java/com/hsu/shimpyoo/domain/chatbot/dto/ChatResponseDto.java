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
    private List<ChatChoice> chatChoices;

    @Getter
    @Setter
    public static class ChatChoice{ // 여러 개의 응답이 있을 수 있고, 그 중에서 선택할 수 있음
        private int index;
        private ChatMessageDto chatMessageDto;
    }
}