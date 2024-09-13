package com.hsu.shimpyoo.domain.chatbot.entity;

import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="CHAT_ROOM")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId; // 사용자 기본키

    @Column(name="chat_title")
    private String chatTitle; // 채팅방 제목

}
