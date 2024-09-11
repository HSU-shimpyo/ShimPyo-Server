package com.hsu.shimpyoo.domain.chatbot.entity;

import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="CHAT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId; // 사용자 기본키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chatting_room_id", nullable = false)
    private ChatRoom chatRoomId; // 채팅방 기본키

    @Column(name="is_send")
    private Boolean isSend; // 수신 여부 (true -> 받은 메시지, false -> 보낸 메시지)

    @Column(name="content")
    private String content; // 메시지의 내용

}
