package com.hsu.shimpyoo.domain.chatbot.service;

import com.hsu.shimpyoo.domain.chatbot.web.dto.ChatQuestionDto;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

public interface ChatService {
    ResponseEntity<CustomAPIResponse<?>> askForChat(ChatQuestionDto chatQuestionDto);

}

