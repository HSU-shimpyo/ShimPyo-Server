package com.hsu.shimpyoo.domain.medicine.web.controller;

import com.hsu.shimpyoo.domain.medicine.web.dto.MedicineRequestDto;
import com.hsu.shimpyoo.domain.medicine.service.MedicineService;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import com.hsu.shimpyoo.global.security.jwt.util.AuthenticationUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medicine")
@RequiredArgsConstructor
public class MedicineController {
    private final MedicineService medicineService;
    private final AuthenticationUserUtils authenticationUserUtils;
    private final UserRepository userRepository;

    @PostMapping("/timeSetting")
    public ResponseEntity<CustomAPIResponse<?>> setMedicineTime(
            @RequestBody MedicineRequestDto dto) {
        // 현재 로그인된 사용자 정보 가져오기
        String loginId = authenticationUserUtils.getCurrentUserId();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));

        // 약 복용 알림 시간 설정
        return medicineService.MedicineTimeSetting(dto, user);
    }

    @GetMapping("/getTimeSetting")
    public ResponseEntity<CustomAPIResponse<?>> getMedicineTimeSetting() {
        String loginId = authenticationUserUtils.getCurrentUserId();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));

        return medicineService.getMedicineTimeSetting(user);
    }

    @GetMapping("/getTimeLeft")
    public ResponseEntity<CustomAPIResponse<?>> getMedicineTimeLeft() {
        String loginId = authenticationUserUtils.getCurrentUserId();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));

        return medicineService.getMedicineTimeLeft(user);
    }
}
