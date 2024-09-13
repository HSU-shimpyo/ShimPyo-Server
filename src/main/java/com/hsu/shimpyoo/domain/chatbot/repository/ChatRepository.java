package com.hsu.shimpyoo.domain.chatbot.repository;

import com.hsu.shimpyoo.domain.chatbot.entity.Chat;
import com.hsu.shimpyoo.domain.chatbot.entity.ChatRoom;
import com.hsu.shimpyoo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    // 사용자 기본키와 채팅방 기본키로 검색하여 가장 최근의 메시지 반환
    Optional<Chat> findTopByUserIdAndChatRoomIdOrderByCreatedAtDesc(User user, ChatRoom chatRoom);

    // 채팅방에서 채팅 상세 조회
    List<Chat> findChatByUserIdAndChatRoomId(User user, ChatRoom chatRoom);
}
