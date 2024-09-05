package com.hsu.shimpyoo.domain.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 값을 포함하지 않도록 설정
public class ChatRequestDto {

    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<Message> messages;

    public ChatRequestDto(String model, String systemMessage, String userMessage) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("system", systemMessage));
        this.messages.add(new Message("user", userMessage));
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
