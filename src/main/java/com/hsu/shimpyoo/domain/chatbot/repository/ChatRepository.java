package com.hsu.shimpyoo.domain.chatbot.repository;

import com.hsu.shimpyoo.domain.chatbot.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
