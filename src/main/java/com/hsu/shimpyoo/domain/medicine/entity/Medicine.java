package com.hsu.shimpyoo.domain.medicine.entity;

import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_timing", nullable = false)
    private MealTiming mealTiming; // 식전 또는 식후

    @Column(name = "intake_timing", nullable = false)
    private Integer intakeTiming; // 약 복용 시간 (곧바로 = 0, 30분 = 30, 1시간 = 60, 2시간 = 120)

    @Column(name = "breakfast")
    private LocalTime breakfast; // 아침 식사

    @Column(name = "lunch")
    private LocalTime lunch; // 점심 식사

    @Column(name = "dinner")
    private LocalTime dinner; // 저녁 식사

    // 약 복용 알림 시간을 계산하는 메서드
    public LocalTime calculateIntakeTime(MealType mealType) {
        LocalTime mealTime = null;

        switch (mealType) {
            case BREAKFAST:
                mealTime = breakfast;
                break;
            case LUNCH:
                mealTime = lunch;
                break;
            case DINNER:
                mealTime = dinner;
                break;
        }

        // 식사 시간이 null이면 null 반환
        if (mealTime == null) {
            return null;
        }

        // 식사 시간에 따른 알림 시간 계산
        int adjustment = mealTiming == MealTiming.BEFORE_MEAL ? -intakeTiming : intakeTiming;
        return mealTime.plusMinutes(adjustment);
    }
}
