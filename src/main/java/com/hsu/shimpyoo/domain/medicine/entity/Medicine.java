package com.hsu.shimpyoo.domain.medicine.entity;

import com.hsu.shimpyoo.domain.medicine.converter.MealTimingConverter;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "MEDICINE")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Medicine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_id")
    private Long medicineId; // 약 복용 기본키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId; // 사용자 기본키

    @Convert(converter = MealTimingConverter.class)
    @Column(name = "meal_timing", nullable = false)
    private MealTiming mealTiming; // 식전 또는 식후

    @Column(name = "intake_timing", nullable = false)
    private Integer intakeTiming; // 약 복용 시간

    @Column(name = "breakfast")
    private LocalTime breakfast; // 아침 식사

    @Column(name = "lunch")
    private LocalTime lunch; // 점심 식사

    @Column(name = "dinner")
    private LocalTime dinner; // 저녁 식사

}
