package com.hsu.shimpyoo.domain.chatbot.service;

import com.hsu.shimpyoo.domain.chatbot.web.dto.ModifyChatRoomTitleDto;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

public interface ChatRoomService {
    // 채팅방 생성
    ResponseEntity<CustomAPIResponse<?>> makeChatRoom();

    // 채팅방 제목 수정
    ResponseEntity<CustomAPIResponse<?>> modifyChatRoomTitle(ModifyChatRoomTitleDto requestDto);

    // 채팅방 목록 조회
    ResponseEntity<CustomAPIResponse<?>> getAllChatRooms();

    // 채팅방 대화 내역 조회
    ResponseEntity<CustomAPIResponse<?>> getChat(Long chatRoomId);

    // 채팅방 제목으로 검색
    ResponseEntity<CustomAPIResponse<?>> getChatRoomByKeyword(String keyword);
}
