package com.hsu.shimpyoo.domain.chatbot.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomListDto {
    // 채팅방 기본키
    private Long chatRoomId;

    // 채팅방 제목
    private String chatRoomTitle;

    // 마지막으로 온 답변
    private String lastChat;

    // 마지막 대화 시간
    private String lastChatAt;


}
