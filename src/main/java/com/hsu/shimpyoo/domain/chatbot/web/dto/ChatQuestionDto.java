package com.hsu.shimpyoo.domain.chatbot.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatQuestionDto {
    private String question;

    private Boolean isSend;
}
