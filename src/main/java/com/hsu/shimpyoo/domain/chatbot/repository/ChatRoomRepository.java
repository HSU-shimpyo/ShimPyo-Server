package com.hsu.shimpyoo.domain.chatbot.repository;

import com.hsu.shimpyoo.domain.chatbot.entity.ChatRoom;
import com.hsu.shimpyoo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 사용자의 모든 채팅방 조회
    List<ChatRoom> findChatRoomByUserId(User user);

    // 채팅방 기본키로 채팅방 조회
    Optional<ChatRoom> findChatRoomByChatRoomId(Long id);

    // 채팅방 제목에 키워드가 포함된 채팅방 조회
    List<ChatRoom> findChatRoomByChatTitleContainingAndUserId(String keyword, User user);
}
