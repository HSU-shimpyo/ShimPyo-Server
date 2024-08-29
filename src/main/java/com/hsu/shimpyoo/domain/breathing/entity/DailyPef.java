package com.hsu.shimpyoo.domain.breathing.entity;

import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DAILY_PEF")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyPef extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_pef_id")
    private Long dailyPefId; // 일별 측정 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId; // 사용자 기본 키

    @Column(name = "pef")
    private Float pef; // 최대호기량

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state; // 상태

    @Enumerated(EnumType.STRING)
    @Column(name = "week_day")
    private WeekDay weekDay; // 요일
}
