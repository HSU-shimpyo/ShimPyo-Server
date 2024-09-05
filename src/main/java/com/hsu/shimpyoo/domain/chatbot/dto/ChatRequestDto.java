package com.hsu.shimpyoo.domain.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDto { // OpenAI API로 보낼 전체 요청을 담는 dto
    private String model;
    private List<ChatMessageDto> chatMessageDtos;

    public ChatRequestDto(String model, String prompt) {
        this.model = model;
        this.chatMessageDtos =  new ArrayList<>();
        this.chatMessageDtos.add(new ChatMessageDto("user", prompt));
    }
}