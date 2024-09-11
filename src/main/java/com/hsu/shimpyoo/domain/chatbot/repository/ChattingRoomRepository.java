package com.hsu.shimpyoo.domain.chatbot.repository;

import com.hsu.shimpyoo.domain.chatbot.entity.ChatRoom;
import com.hsu.shimpyoo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChattingRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findChatRoomByUserId(User user);
}
