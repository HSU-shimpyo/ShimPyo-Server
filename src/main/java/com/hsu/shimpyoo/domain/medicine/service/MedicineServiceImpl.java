package com.hsu.shimpyoo.domain.medicine.service;

import com.hsu.shimpyoo.domain.medicine.dto.MedicineRequestDto;
import com.hsu.shimpyoo.domain.medicine.entity.MealType;
import com.hsu.shimpyoo.domain.medicine.entity.Medicine;
import com.hsu.shimpyoo.domain.medicine.repository.MedicineRepository;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl {

    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<CustomAPIResponse<?>> MedicineTimeSetting(MedicineRequestDto dto) {
        try {
            // Medicine 엔티티 생성
            Medicine medicine = Medicine.builder()
                    .userId(userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("사용자 없음")))
                    .mealTiming(dto.getMealTiming())
                    .intakeTiming(dto.getIntakeTiming())
                    .breakfast(dto.getBreakfast())
                    .lunch(dto.getLunch())
                    .dinner(dto.getDinner())
                    .build();

            // 각 식사 시간에 대해 알림 시간 계산
            LocalTime breakfastAlertTime = dto.getBreakfast() != null ? medicine.calculateIntakeTime(MealType.BREAKFAST) : null;
            LocalTime lunchAlertTime = dto.getLunch() != null ? medicine.calculateIntakeTime(MealType.LUNCH) : null;
            LocalTime dinnerAlertTime = dto.getDinner() != null ? medicine.calculateIntakeTime(MealType.DINNER) : null;

            // 계산된 알림 시간을 다시 Medicine 엔티티에 설정
            medicine = Medicine.builder()
                    .userId(medicine.getUserId())
                    .mealTiming(medicine.getMealTiming())
                    .intakeTiming(medicine.getIntakeTiming())
                    .breakfast(breakfastAlertTime)
                    .lunch(lunchAlertTime)
                    .dinner(dinnerAlertTime)
                    .build();

            // Medicine 엔티티 저장
            medicineRepository.save(medicine);

            // 성공 응답 생성
            CustomAPIResponse<String> response = CustomAPIResponse.createSuccess(200, null, "약 복용 시간이 성공적으로 설정되었습니다.");

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            // 예외 발생 시 실패 응답 생성
            CustomAPIResponse<String> response = CustomAPIResponse.createFailWithout(400, "약 복용 시간 설정에 실패했습니다.");

            return ResponseEntity.status(400).body(response);
        }
    }
}
