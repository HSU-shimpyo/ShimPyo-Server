package com.hsu.shimpyoo.domain.chatbot.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatQuestionDto {
    // 채팅방 기본키
    @NotNull(message = "채팅방 기본키를 입력해주세요")
    private Long chatRoomId;

    // 채팅방 질문
    @NotNull(message = "질문을 입력해주세요")
    private String question;
}
