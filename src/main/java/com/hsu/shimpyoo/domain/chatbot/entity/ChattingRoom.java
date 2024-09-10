package com.hsu.shimpyoo.domain.chatbot.entity;

import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="CHATTING_ROOM")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChattingRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatting_room_id")
    private Long chattingRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId; // 사용자 기본키

    @Column(name="chatting_title")
    private String chattingTitle; // 채팅방 제목

}
