package com.hsu.shimpyoo.domain.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRequestDto {

    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<Message> messages;

    // 사용자 메시지만을 받아서 자동으로 model과 messages를 구성하는 생성자
    public ChatRequestDto(String model, String realMessage) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", realMessage));
    }

    public static class Message {
        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

}
