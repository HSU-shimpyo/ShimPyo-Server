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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breathing_file_id", nullable = false)
    private BreathingFile breathingFileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId; // 사용자 기본키

    @Column(name = "breathing_rate")
    private Double breathingRate; // 호흡 수치

    @Column(name = "first")
    private Double first; // 1회차

    @Column(name = "second")
    private Double second; // 2회차

    @Column(name = "third")
    private Double third; // 3회차
}

