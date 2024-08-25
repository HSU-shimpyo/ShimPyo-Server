package com.hsu.shimpyoo.domain.breathing.service;
import com.hsu.shimpyoo.domain.breathing.entity.Breathing;
import com.hsu.shimpyoo.domain.breathing.repository.BreathingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BreathingService {
    private final BreathingRepository breathingRepository;

    public String evaluateBreathingRate(Long breathingId) {
        // Breathing 엔티티를 ID로 조회
        Breathing oldBreathing = breathingRepository.findById(breathingId)
                .orElseThrow(() -> new RuntimeException("호흡 기록을 찾을 수 없습니다."));

        // first, second, third 중에서 최대값 계산
        Float maxBreathingRate = Math.max(oldBreathing.getFirst(), Math.max(oldBreathing.getSecond(), oldBreathing.getThird()));

        // 이전의 breathing_rate 가져오기
        Float previousBreathingRate = oldBreathing.getBreathingRate();

        // 새로운 Breathing 객체 생성 및 데이터 복사
        Breathing newBreathing = Breathing.builder()
                .userId(oldBreathing.getUserId())
                .first(oldBreathing.getFirst())
                .second(oldBreathing.getSecond())
                .third(oldBreathing.getThird())
                .breathingRate(maxBreathingRate)
                .build();

        // 새로운 기록을 데이터베이스에 저장
        breathingRepository.save(newBreathing);

        // 비교를 기반으로 상태 결정
        if (previousBreathingRate == null) {
            return "이전 데이터 없음";
        } else {
            float percentage = (maxBreathingRate / previousBreathingRate) * 100;
            if (percentage >= 80) {
                return "안정";
            } else if (percentage >= 60) {
                return "주의";
            } else {
                return "위험";
            }
        }
    }

}
