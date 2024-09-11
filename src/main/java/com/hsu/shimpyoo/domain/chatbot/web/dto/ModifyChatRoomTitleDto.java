package com.hsu.shimpyoo.domain.chatbot.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyChatRoomTitleDto {
    // 새로운 제목
    @NotNull(message = "채팅방 제목을 입력해주세요")
    private String title;

    // 채팅방 기본키
    @NotNull(message = "채팅방 기본키를 입력해주세요")
    private Long chatRoomId;
}
