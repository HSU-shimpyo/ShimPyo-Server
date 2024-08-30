package com.hsu.shimpyoo.domain.breathing.web.controller;

import com.hsu.shimpyoo.domain.breathing.entity.Breathing;
import com.hsu.shimpyoo.domain.breathing.service.BreathingService;
import com.hsu.shimpyoo.domain.breathing.web.dto.BreathingFlaskRequestDto;
import com.hsu.shimpyoo.domain.breathing.entity.BreathingFile;
import com.hsu.shimpyoo.domain.breathing.service.BreathingCheckServiceImpl;
import com.hsu.shimpyoo.domain.breathing.web.dto.BreathingUploadRequestDto;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import com.hsu.shimpyoo.global.security.jwt.util.AuthenticationUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/breathing/check")
@RequiredArgsConstructor
public class BreathingCheckController {
    private final BreathingCheckServiceImpl breathingCheckServiceImpl;
    private final BreathingService breathingService;
    private final AuthenticationUserUtils authenticationUserUtils;
    private final UserRepository userRepository;


    // 녹음 파일 업로드
    @PostMapping("/analyze")
    public CustomAPIResponse<Map<String, Object>> analyzePef(
            @RequestPart("date") String date,
            @RequestPart("firstFile") MultipartFile firstFile,
            @RequestPart("secondFile") MultipartFile secondFile,
            @RequestPart("thirdFile") MultipartFile thirdFile) throws IOException {

        // 오류로 인해 RequestPart로 받은 후, DTO로 변환
        BreathingUploadRequestDto breathingUploadRequestDto = BreathingUploadRequestDto.builder()
                .date(date)
                .firstFile(firstFile)
                .secondFile(secondFile)
                .thirdFile(thirdFile)
                .build();

        BreathingFile breathingFile = breathingCheckServiceImpl.uploadBreathing(breathingUploadRequestDto);

        BreathingFlaskRequestDto breathingFlaskRequestDto = BreathingFlaskRequestDto.builder()
                .firstFile(breathingFile.getFirstUrl())
                .secondFile(breathingFile.getSecondUrl())
                .thirdFile(breathingFile.getThirdUrl())
                .build();

        Long breathingFileId= breathingFile.getBreathingFileId();

        // flask 서버로 파일 URL을 전송하고, Breathing 결과를 받아옴
        Breathing todayBreathing = breathingCheckServiceImpl.analyzeBreathing(breathingFlaskRequestDto, breathingFileId);

        // 현재 로그인된 사용자 정보 가져오기
        String loginId = authenticationUserUtils.getCurrentUserId();
        Optional<User> isExistUser = userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        // 최종 결과 반환
        return breathingService.calculateBreathingResult(todayBreathing, isExistUser.get());
    }
}
