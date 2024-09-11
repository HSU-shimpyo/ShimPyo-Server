package com.hsu.shimpyoo.domain.chatbot.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyChatRoomTitleDto {
    // 새로운 제목
    private String title;

    // 채팅방 기본키
    private Long chatRoomId;
}
