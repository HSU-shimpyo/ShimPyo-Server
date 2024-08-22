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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineService {

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

    public ResponseEntity<CustomAPIResponse<?>> getMedicineTimeLeft(User user) {
        try {
            // 해당 사용자의 약 복용 시간 정보 가져오기
            Medicine medicine = medicineRepository.findByUserId(user)
                    .orElseThrow(() -> new RuntimeException("약 복용 시간 설정이 존재하지 않습니다."));

            // 현재 시간 가져오기
            LocalTime now = LocalTime.now();

            // 남은 시간 계산
            Long minutesLeft = calculateTimeLeft(now, medicine);

            if (minutesLeft == null) {
                CustomAPIResponse<String> response = CustomAPIResponse.createFailWithout(404, "복용할 시간이 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // 남은 시간을 "X시간 Y분" 형식으로 변환
            String timeLeftStr = formatTimeLeft(minutesLeft);

            // 성공 응답 생성
            CustomAPIResponse<String> response = CustomAPIResponse.createSuccess(200, timeLeftStr, "남은 약 복용 시간이 성공적으로 계산되었습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (RuntimeException e) {
            // RuntimeException 발생 시 처리
            CustomAPIResponse<String> response = CustomAPIResponse.createFailWithout(404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            // 기타 예외 발생 시 처리
            CustomAPIResponse<String> response = CustomAPIResponse.createFailWithout(400, "남은 약 복용 시간 계산에 실패했습니다. ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    private Long calculateTimeLeft(LocalTime now, Medicine medicine) {
        List<LocalTime> times = new ArrayList<>();
        if (medicine.getBreakfast() != null) times.add(medicine.getBreakfast());
        if (medicine.getLunch() != null) times.add(medicine.getLunch());
        if (medicine.getDinner() != null) times.add(medicine.getDinner());

        // 만약 모든 시간이 null이라면 null을 반환하여 처리
        return times.stream()
                .map(time -> ChronoUnit.MINUTES.between(now, time))
                .filter(minutes -> minutes >= 0)
                .min(Long::compareTo)
                .orElse(null);
    }

    private String formatTimeLeft(Long minutesLeft) {
        if (minutesLeft < 60) {
            return minutesLeft + "분";
        } else {
            long hours = minutesLeft / 60;
            long minutes = minutesLeft % 60;
            return hours + "시간" + (minutes > 0 ? " " + minutes + "분" : "");
        }
    }

}
