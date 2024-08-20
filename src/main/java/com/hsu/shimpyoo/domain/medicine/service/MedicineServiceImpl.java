package com.hsu.shimpyoo.domain.medicine.service;

import com.hsu.shimpyoo.domain.medicine.dto.MedicineRequestDto;
import com.hsu.shimpyoo.domain.medicine.entity.MealTiming;
import com.hsu.shimpyoo.domain.medicine.entity.Medicine;
import com.hsu.shimpyoo.domain.medicine.repository.MedicineRepository;
import com.hsu.shimpyoo.domain.user.entity.User;
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

    @Transactional
    public ResponseEntity<CustomAPIResponse<?>> MedicineTimeSetting(MedicineRequestDto dto, User user) {
        try {
            // 기존의 약 복용 시간 설정 정보 삭제
            medicineRepository.deleteByUserId(user);

            // Medicine 엔티티 생성 및 알림 시간 계산
            Medicine medicine = Medicine.builder()
                    .userId(user)
                    .mealTiming(dto.getMealTiming())
                    .intakeTiming(dto.getIntakeTiming())
                    .breakfast(dto.getBreakfast() != null ? calculateIntakeTime(dto.getBreakfast(), dto.getMealTiming(), dto.getIntakeTiming()) : null)
                    .lunch(dto.getLunch() != null ? calculateIntakeTime(dto.getLunch(), dto.getMealTiming(), dto.getIntakeTiming()) : null)
                    .dinner(dto.getDinner() != null ? calculateIntakeTime(dto.getDinner(), dto.getMealTiming(), dto.getIntakeTiming()) : null)
                    .build();

            // Medicine 엔티티 저장
            medicineRepository.save(medicine);

            // 성공 응답 생성
            CustomAPIResponse<String> response = CustomAPIResponse.createSuccess(200, null, "약 복용 시간이 성공적으로 설정되었습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            // 예외 발생 시 실패 응답 생성
            CustomAPIResponse<String> response = CustomAPIResponse.createFailWithout(400, "약 복용 시간 설정에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    private LocalTime calculateIntakeTime(LocalTime mealTime, MealTiming mealTiming, Integer intakeTiming) {
        int adjustment = mealTiming == MealTiming.BEFORE_MEAL ? -intakeTiming : intakeTiming;
        return mealTime.plusMinutes(adjustment);
    }

}
