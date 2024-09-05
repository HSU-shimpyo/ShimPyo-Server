package com.hsu.shimpyoo.domain.chatbot.controller;

import com.hsu.shimpyoo.domain.chatbot.dto.ChatRequestDto;
import com.hsu.shimpyoo.domain.chatbot.dto.ChatResponseDto;
import com.hsu.shimpyoo.domain.chatbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
class ChatController {
    private final ChatService chatService;

    @PostMapping("/ask")
    public ResponseEntity<ChatResponseDto> askChat(@RequestBody ChatRequestDto chatRequestDto) {
        ChatResponseDto chatResponseDto = chatService.askForChat(chatRequestDto);
        return ResponseEntity.ok(chatResponseDto);
    }
}
