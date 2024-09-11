package com.hsu.shimpyoo.domain.chatbot.web.dto;

import com.hsu.shimpyoo.global.enums.TFStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatListDto {
    // 내용
    private String content;

    // 수신 및 발신 여부
    private TFStatus isSend;
}
