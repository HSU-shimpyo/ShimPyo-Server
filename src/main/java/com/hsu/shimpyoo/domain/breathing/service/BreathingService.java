package com.hsu.shimpyoo.domain.breathing.service;
import com.hsu.shimpyoo.domain.breathing.web.dto.BreathingPefDto;
import com.hsu.shimpyoo.domain.breathing.entity.Breathing;
import com.hsu.shimpyoo.domain.breathing.entity.*;
import com.hsu.shimpyoo.domain.breathing.repository.BreathingRepository;
import com.hsu.shimpyoo.domain.breathing.repository.DailyPefRepository;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BreathingService {
    private final BreathingRepository breathingRepository;
    private final UserRepository userRepository;
    private final DailyPefRepository dailyPefRepository;

    public CustomAPIResponse<Map<String, Object>> calculateBreathingResult(BreathingPefDto dto, User user) {
        // 가장 최근의 Breathing 데이터를 userId로 조회
        Breathing recentBreathing = breathingRepository.findTopByUserIdOrderByCreatedAtDesc(user);

        // 최대호기량 설정
        Double maxBreathingRate = Math.max(dto.getFirst(), Math.max(dto.getSecond(), dto.getThird()));

        // 새로운 Breathing 데이터 저장
        Breathing breathing = Breathing.builder()
                .userId(user)
                .breathingRate(maxBreathingRate)
                .first(dto.getFirst())
                .second(dto.getSecond())
                .third(dto.getThird())
                .build();
        breathingRepository.save(breathing);

        // 상태 결정
        State state;
        String rateChangeDirection = "";  // 증가 또는 감소 방향
        int rateDifferencePercent = 0;

        if (recentBreathing != null) {
            Double previousBreathingRate = recentBreathing.getBreathingRate();
            rateDifferencePercent = (int) Math.round(((maxBreathingRate - previousBreathingRate) / previousBreathingRate) * 100);

            if (rateDifferencePercent >= 0) {
                rateChangeDirection = "증가";
            } else {
                rateChangeDirection = "감소";
                rateDifferencePercent = Math.abs(rateDifferencePercent); // 절대값으로 변환
            }

            double rateChange = (maxBreathingRate / previousBreathingRate) * 100;
            if (rateChange >= 80) {
                state = State.GOOD;
            } else if (rateChange >= 60) {
                state = State.WARNING;
            } else {
                state = State.DANGER;
            }
        } else {
            // 이전 데이터가 없을 때
            return CustomAPIResponse.createFailWithout(404, "이전 데이터를 찾을 수 없습니다.");
        }

        // 현재 요일에 해당하는 WeekDay Enum을 가져옵니다.
        WeekDay currentWeekDay = WeekDay.valueOf(LocalDateTime.now().getDayOfWeek().name());

        // DailyPef 데이터베이스에 상태와 최대호기량을 저장
        DailyPef dailyPef = DailyPef.builder()
                .userId(user)
                .pef(maxBreathingRate)
                .state(state) // Enum 타입으로 상태 저장
                .weekDay(currentWeekDay) // Enum 타입으로 요일 저장
                .build();
        dailyPefRepository.save(dailyPef);

        // 반환 데이터
        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("status", state.getDescription()); // 한국어 설명으로 반환
        responseData.put("breathingRate", maxBreathingRate);
        responseData.put("rateDifference", rateDifferencePercent + "% " + rateChangeDirection);
        responseData.put("first", breathing.getFirst());
        responseData.put("second", breathing.getSecond());
        responseData.put("third", breathing.getThird());

        return CustomAPIResponse.createSuccess(200, responseData, "오늘의 쉼 결과 조회에 성공했습니다.");
    }

    public List<Map<String, Object>> getWeeklyBreathingRates(User user) {
        LocalDateTime today = LocalDateTime.now();
        List<Map<String, Object>> weeklyBreathingRates = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDateTime targetDate = today.minusDays(i);
            LocalDateTime startOfDay = targetDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfDay = targetDate.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

            Optional<Breathing> breathingOptional = breathingRepository.findTopByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(user, startOfDay, endOfDay);

            Map<String, Object> data = new HashMap<>();
            data.put("date", targetDate.toLocalDate());
            data.put("breathingRate", breathingOptional.map(Breathing::getBreathingRate).orElse(null));
            weeklyBreathingRates.add(data);
        }

        return weeklyBreathingRates;
    }

    // 나의 최대호기량 조회 (마이페이지)
    public ResponseEntity<CustomAPIResponse<?>> getMostRecentBreathingRate(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));


        Breathing recentBreathing = breathingRepository.findTopByUserIdOrderByCreatedAtDesc(user);

        if (recentBreathing != null) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("breathingRate", recentBreathing.getBreathingRate());
            return ResponseEntity.ok(CustomAPIResponse.createSuccess(200, responseData, "나의 기준 최대호기량 조회에 성공했습니다."));
        } else {
            double breathingRate = user.getPef();
            return ResponseEntity.ok(CustomAPIResponse.createSuccess(200, breathingRate, "나의 기준 최대호기량 조회에 성공했습니다."));
        }
    }

    public CustomAPIResponse<Map<String, Object>> getWeeklyBreathingAverage(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate();
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY).toLocalDate();

        List<Map<String, Object>> weeklyData = new ArrayList<>();
        float totalBreathingRate = 0;
        int count = 0;

        for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {
            WeekDay weekDay = WeekDay.valueOf(date.getDayOfWeek().name());

            // 해당 날짜의 데이터를 조회
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            DailyPef dailyPef = dailyPefRepository.findTopByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(user, startOfDay, endOfDay)
                    .orElse(null);

            Map<String, Object> dayData = new LinkedHashMap<>();
            dayData.put("weekDay", weekDay.getKoreanName());
            dayData.put("date", date.toString());
            if (dailyPef != null) {
                dayData.put("breathingRate", dailyPef.getPef());
                totalBreathingRate += dailyPef.getPef();
                count++;
            } else {
                dayData.put("breathingRate", null);
            }
            weeklyData.add(dayData);
        }

        float averageBreathingRate = count > 0 ? totalBreathingRate / count : 0;

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("weeklyData", weeklyData);
        data.put("averagePef", Math.round(averageBreathingRate));

        return CustomAPIResponse.createSuccess(200, data, "주간 최대호기량 및 평균 조회에 성공했습니다.");
    }

    // 주간 평균 최대호기량 비교
    public CustomAPIResponse<Map<String, Object>> getWeeklyBreathingDifference(User user) {
        // 이번 주와 지난 주의 최대호기량 평균 계산
        double thisWeekAverage = calculateWeeklyAverage(user, LocalDate.now().with(DayOfWeek.MONDAY), LocalDate.now());
        double lastWeekAverage = calculateWeeklyAverage(user, LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(1), LocalDate.now().with(DayOfWeek.SUNDAY).minusWeeks(1));

        String state; // 증가, 유지, 감소
        int differencePercent; // 증가율

        if (lastWeekAverage == 0) {
            differencePercent = thisWeekAverage > 0 ? 100 : 0;
            state = thisWeekAverage > 0 ? "증가" : "유지";
        } else {
            differencePercent = (int) Math.abs(Math.round(((thisWeekAverage - lastWeekAverage) / lastWeekAverage) * 100));
            state = thisWeekAverage > lastWeekAverage ? "증가" : (thisWeekAverage < lastWeekAverage ? "감소" : "유지");
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("lastWeekAverage", lastWeekAverage);
        data.put("thisWeekAverage", thisWeekAverage);
        data.put("differencePercent", differencePercent);
        data.put("state", state);

        return CustomAPIResponse.createSuccess(200, data, "주간 평균 최대호기량 비교 결과 조회에 성공했습니다.");
    }

    private double calculateWeeklyAverage(User user, LocalDate startOfWeek, LocalDate endOfWeek) {
        return startOfWeek.datesUntil(endOfWeek.plusDays(1))
                .map(date -> dailyPefRepository.findTopByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                                user, date.atStartOfDay(), date.atTime(LocalTime.MAX))
                        .map(DailyPef::getPef).orElse(null))
                .filter(Objects::nonNull)
                .mapToDouble(f -> f.doubleValue())
                .average()
                .orElse(0.0);
    }
}
