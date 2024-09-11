package com.hsu.shimpyoo.domain.chatbot.entity;

import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.global.entity.BaseEntity;
import com.hsu.shimpyoo.global.enums.TFStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="CHAT")
@Getter
@Setter
@Builder
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

    @Enumerated(EnumType.STRING)
    @Column(name="is_send")
    private TFStatus isSend; // 수신 여부 (true -> 받은 메시지, false -> 보낸 메시지)

    @Column(name="content", columnDefinition = "TEXT")
    private String content; // 메시지의 내용

}
