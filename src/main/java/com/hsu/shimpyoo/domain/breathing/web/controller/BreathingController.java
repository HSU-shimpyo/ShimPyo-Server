package com.hsu.shimpyoo.domain.breathing.web.controller;

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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/breathing")
@RequiredArgsConstructor
public class BreathingController {
    private final BreathingService breathingService;
    private final AuthenticationUserUtils authenticationUserUtils;
    private final UserRepository userRepository;

//    // 오늘의 쉼 결과
//    @PostMapping("/today/result")
//    public CustomAPIResponse<Map<String, Object>> getTodayBreathingResult(
//            @RequestBody BreathingPefDto dto) {
//        // 현재 로그인된 사용자 정보 가져오기
//        String loginId = authenticationUserUtils.getCurrentUserId();
//        User user = userRepository.findByLoginId(loginId)
//                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));
//
//        return breathingService.calculateBreathingResult(dto, user);
//    }

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

}
