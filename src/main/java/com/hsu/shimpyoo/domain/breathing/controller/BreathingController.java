package com.hsu.shimpyoo.domain.breathing.controller;

import com.hsu.shimpyoo.domain.breathing.dto.BreathingRequestDto;
import com.hsu.shimpyoo.domain.breathing.service.BreathingService;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import com.hsu.shimpyoo.global.security.jwt.util.AuthenticationUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/breathing")
@RequiredArgsConstructor
public class BreathingController {
    private final BreathingService breathingService;
    private final AuthenticationUserUtils authenticationUserUtils;
    private final UserRepository userRepository;

    // 오늘의 쉼 결과
    @PostMapping("/today/result")
    public CustomAPIResponse<Map<String, Object>> getTodayBreathingResult(
            @RequestBody BreathingRequestDto dto) {
        // 현재 로그인된 사용자 정보 가져오기
        String loginId = authenticationUserUtils.getCurrentUserId();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));

        return breathingService.calculateBreathingResult(dto, user);
    }
}
