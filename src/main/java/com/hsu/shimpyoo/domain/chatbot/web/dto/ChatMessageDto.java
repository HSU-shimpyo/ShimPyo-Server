package com.hsu.shimpyoo.domain.chatbot.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto { //  OpenAI API와의 통신에서 메시지의 역할과 내용을 담는 dto
    private String role;
    private String content;
}
