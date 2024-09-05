package com.hsu.shimpyoo.domain.chatbot.service;

import com.hsu.shimpyoo.domain.chatbot.dto.ChatRequestDto;
import com.hsu.shimpyoo.domain.chatbot.dto.ChatResponseDto;


public interface ChatService {
    ChatResponseDto askForChat(ChatRequestDto chatRequestDto);

}

