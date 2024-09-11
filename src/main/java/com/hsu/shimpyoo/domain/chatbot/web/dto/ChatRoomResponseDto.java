package com.hsu.shimpyoo.domain.chatbot.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponseDto {
    // 채팅방 기본키
    private Long chatRoomId;

    // 채팅방 제목
    private String chatRoomTitle;

    // 마지막으로 온 답변
    private String lastChat;

    // 생성 시간
    private LocalDateTime createdAt;

    // 생성 시간을 'yyyy.MM.dd' 형식으로 반환
    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return createdAt.format(formatter);
    }


}
