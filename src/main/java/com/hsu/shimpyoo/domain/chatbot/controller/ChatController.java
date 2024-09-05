package com.hsu.shimpyoo.domain.chatbot.controller;

import com.hsu.shimpyoo.domain.chatbot.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 사용자 입력 메시지를 받아서 처리
    @PostMapping("/ask")
    public ResponseEntity<String> askChat(@RequestBody String userMessage) {
        try {
            // ChatService를 호출하여 메시지 처리
            String responseMessage = chatService.askForChat(userMessage);

            // 성공 시 응답 반환
            return ResponseEntity.ok(responseMessage);
        } catch (Exception e) {
            // 오류 발생 시 500 Internal Server Error와 함께 메시지 반환
            return ResponseEntity.status(500).body("API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

