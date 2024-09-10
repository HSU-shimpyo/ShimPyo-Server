package com.hsu.shimpyoo.domain.chatbot.web.controller;

import com.hsu.shimpyoo.domain.chatbot.web.dto.ChatQuestionDto;
import com.hsu.shimpyoo.domain.chatbot.service.ChatService;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 채팅방 생성
    @PostMapping("/makeChattingRoom")
    public ResponseEntity<CustomAPIResponse<?>> makeChattingRoom(){
        ResponseEntity<CustomAPIResponse<?>> result=chatService.makeChattingRoom();
        return result;
    }

    // 사용자 입력 메시지를 받아서 처리
    @PostMapping("/ask")
    public ResponseEntity<CustomAPIResponse<?>> askChat(@RequestBody ChatQuestionDto chatQuestionDto) {
        try {
            // ChatService를 호출하여 메시지 처리
            ResponseEntity<CustomAPIResponse<?>> result= chatService.askForChat(chatQuestionDto.getQuestion());

            // 성공 시 응답 반환
            return result;
        } catch (Exception e) {
            // 오류 발생 시 500 Internal Server Error와 함께 메시지 반환
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"API 호출 중 오류가 발생했습니다.");
        }
    }
}

