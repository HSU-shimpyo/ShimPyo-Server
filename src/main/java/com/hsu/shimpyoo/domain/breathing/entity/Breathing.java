package com.hsu.shimpyoo.domain.breathing.entity;

import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BREATHING")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Breathing extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "breathing_id")
    private Long breathingId; // 호흡 측정 기본키

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "user_id", nullable = false)
    private User userId; // 사용자 기본키

    @Column(name = "breathing_rate", nullable = false)
    private Float breathingRate; // 호흡 수치
}

