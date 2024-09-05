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

    // 다른 생성자나 메서드에서 chatMessageDtos가 null로 초기화되지 않도록, 필드를 선언할 때 바로 빈 리스트로 초기화
    private List<ChatMessageDto> chatMessageDtos=new ArrayList<>();

    public ChatRequestDto(String model, String prompt) {
        this.model = model != null ? model : "gpt-3.5-turbo";  // 기본 모델 지정
        this.chatMessageDtos =  new ArrayList<>();
        this.chatMessageDtos.add(new ChatMessageDto("user", prompt));
    }
}