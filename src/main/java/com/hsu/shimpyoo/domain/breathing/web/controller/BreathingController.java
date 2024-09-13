package com.hsu.shimpyoo.domain.breathing.web.controller;

import com.hsu.shimpyoo.domain.breathing.entity.Breathing;
import com.hsu.shimpyoo.domain.breathing.repository.BreathingRepository;
import com.hsu.shimpyoo.domain.breathing.service.BreathingService;
import com.hsu.shimpyoo.domain.breathing.web.dto.BreathingPefDto;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import com.hsu.shimpyoo.global.security.jwt.util.AuthenticationUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/breathing")
@RequiredArgsConstructor
public class BreathingController {
    private final BreathingService breathingService;
    private final AuthenticationUserUtils authenticationUserUtils;
    private final UserRepository userRepository;
    private final BreathingRepository breathingRepository;

    // 오늘 측정한 호흡 기록을 찾아서, 결과를 조회
    @GetMapping("/today/result")
    public CustomAPIResponse<Map<String, Object>> getTodayBreathingResult() {
         //현재 로그인된 사용자 정보 가져오기
        String loginId = authenticationUserUtils.getCurrentUserId();
        User isExistUser= userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));

        // 오늘 측정한 기록이 있는지 조회
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay(); // 오늘의 시작 시간 00:00:00
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX); // 오늘의 끝 시간 23:59:59
        Optional<Breathing> isExistBreathing = breathingRepository.findByUserIdAndCreatedAtBetween(
                isExistUser, startOfToday, endOfToday);

        // 있다면 계산
        if (isExistBreathing.isPresent()) {
            return breathingService.calculateBreathingResult(isExistBreathing.get(), isExistUser);
        }

        // 없다면 다른 응답을 반환
        return CustomAPIResponse.createSuccess(200, null, "오늘의 측정 기록이 존재하지 않습니다.");
    }

    // 오늘을 기준으로 지난 7일간의 쉼 결과 조회
    @GetMapping("/today/weekly")
    public ResponseEntity<CustomAPIResponse<?>> getTodayBreathingWeekly() {
        // 현재 로그인된 사용자 정보 가져오기
        String loginId = authenticationUserUtils.getCurrentUserId();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));

        // 지난 7일간의 breathingRate 가져오기
        List<Map<String, Object>> weeklyBreathingRates = breathingService.getWeeklyBreathingRates(user);

        return ResponseEntity.ok(CustomAPIResponse.createSuccess(200, weeklyBreathingRates, "지난 7일간의 최대호기량 조회에 성공했습니다."));
    }

    // 나의 최대호기량 조회 (마이페이지)
    @GetMapping("/myBreathingRate")
    public ResponseEntity<CustomAPIResponse<?>> getMyBreathingRate() {
        String loginId = authenticationUserUtils.getCurrentUserId();
        return breathingService.getMostRecentBreathingRate(loginId);
    }

    // 주간 평균 최대호기량 조회
    @GetMapping("/weekly/average")
    public ResponseEntity<CustomAPIResponse<?>> getWeeklyBreathingAverage() {
        String loginId = authenticationUserUtils.getCurrentUserId();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));

        CustomAPIResponse<Map<String, Object>> response = breathingService.getWeeklyBreathingAverage(user);
        return ResponseEntity.ok(response);
    }

    // 주간 평균 최대호기량 비교
    @GetMapping("/weekly/difference")
    public ResponseEntity<CustomAPIResponse<?>> getWeeklyBreathingDifference() {
        String loginId = authenticationUserUtils.getCurrentUserId();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));

        CustomAPIResponse<Map<String, Object>> response = breathingService.getWeeklyBreathingDifference(user);

        return ResponseEntity.ok(response);
    }

    // 이번주 쉼 상태
    @GetMapping("/weekly/state")
    public ResponseEntity<CustomAPIResponse<?>> getWeeklyBreathingState() {
        String loginId = authenticationUserUtils.getCurrentUserId();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));

        CustomAPIResponse<Map<String, Object>> response = breathingService.getWeeklyBreathingState(user);
        return ResponseEntity.ok(response);
    }

    // 월간 평균 최대호기량
    @GetMapping("/monthly/average")
    public ResponseEntity<CustomAPIResponse<?>> getMonthlyBreathingAverage() {
        String loginId = authenticationUserUtils.getCurrentUserId();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));

        CustomAPIResponse<Map<String, Object>> response = breathingService.getMonthlyBreathingAverage(user);
        return ResponseEntity.ok(response);
    }

    // 월간 평균 최대호기량 비교
    @GetMapping("/monthly/difference")
    public ResponseEntity<CustomAPIResponse<?>> getMonthlyBreathingDifference() {
        String loginId = authenticationUserUtils.getCurrentUserId();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));

        CustomAPIResponse<Map<String, Object>> response = breathingService.getMonthlyBreathingDifference(user);
        return ResponseEntity.ok(response);
    }

    // 이번달 쉼 상태
    @GetMapping("/monthly/state")
    public ResponseEntity<CustomAPIResponse<?>> getMonthlyBreathingState() {
        String loginId = authenticationUserUtils.getCurrentUserId();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));

        CustomAPIResponse<Map<String, Object>> response = breathingService.getMonthlyBreathingState(user);
        return ResponseEntity.ok(response);
    }

}
