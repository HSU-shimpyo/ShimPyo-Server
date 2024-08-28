package com.hsu.shimpyoo.domain.breathing.controller;

import com.hsu.shimpyoo.domain.breathing.dto.BreathingRequestDto;
import com.hsu.shimpyoo.domain.breathing.dto.BreathingUploadRequestDto;
import com.hsu.shimpyoo.domain.breathing.service.BreathingService;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import com.hsu.shimpyoo.global.security.jwt.util.AuthenticationUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/breathing")
@RequiredArgsConstructor
public class BreathingController {
    private final BreathingService breathingService;
    private final AuthenticationUserUtils authenticationUserUtils;
    private final UserRepository userRepository;

    // 녹음 파일 업로드
    @PostMapping("/uploadFile")
    public ResponseEntity<CustomAPIResponse<?>> uploadFile(
            @RequestPart("date") String date,
            @RequestPart("firstFile") MultipartFile firstFile,
            @RequestPart("secondFile") MultipartFile secondFile,
            @RequestPart("thirdFile") MultipartFile thirdFile) throws IOException {

        // 오류로 인해 RequestPart로 받은 후, DTO로 변환
        BreathingUploadRequestDto breathingUploadRequestDto = new BreathingUploadRequestDto(date, firstFile, secondFile, thirdFile);

        ResponseEntity<CustomAPIResponse<?>> result=breathingService.uploadBreathing(breathingUploadRequestDto);
        return result;
    }

    // 오늘의 쉼 결과
    @PostMapping("/todayResult")
    public CustomAPIResponse<Map<String, Object>> getTodayBreathingResult(
            @RequestBody BreathingRequestDto dto) {
        // 현재 로그인된 사용자 정보 가져오기
        String loginId = authenticationUserUtils.getCurrentUserId();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));

        return breathingService.calculateBreathingResult(dto, user);
    }

    // 오늘을 기준으로 지난 7일간의 쉼 결과 조회
    @GetMapping("/todayWeekly")
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
}
