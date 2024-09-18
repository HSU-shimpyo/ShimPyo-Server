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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/breathing/check")
@RequiredArgsConstructor
@Validated
public class BreathingCheckController {
    private final BreathingCheckServiceImpl breathingCheckServiceImpl;
    private final BreathingService breathingService;
    private final AuthenticationUserUtils authenticationUserUtils;
    private final UserRepository userRepository;


    // 녹음 파일 업로드 + pef 추출 + 수치를 계산하여 반환
    @PostMapping("/getPef")
    public CustomAPIResponse<Map<String, Object>> getPef(
            @RequestPart("date")
            @NotEmpty(message = "날짜를 입력해주세요")
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "날짜 입력 형식을 맞춰주세요")
            String date,
            @RequestPart("firstFile") @NotNull(message = "첫번째 호흡 파일을 업로드 해주세요")
            MultipartFile firstFile,
            @RequestPart("secondFile") @NotNull(message = "두번째 호흡 파일을 업로드 해주세요")
            MultipartFile secondFile,
            @RequestPart("thirdFile") @NotNull(message = "세번째 호흡 파일을 업로드 해주세요")
            MultipartFile thirdFile) throws IOException {

        // 오류로 인해 파일이 비어 있는지 수동으로 검증
        if (firstFile == null || firstFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "첫번째 호흡 파일을 업로드 해주세요");
        }
        if (secondFile == null || secondFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "두번째 호흡 파일을 업로드 해주세요");
        }
        if (thirdFile == null || thirdFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "세번째 호흡 파일을 업로드 해주세요");
        }

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
        Breathing todayBreathing = breathingCheckServiceImpl.analyzeBreathing(breathingUploadRequestDto, breathingFileId);

        // 현재 로그인된 사용자 정보 가져오기
        String loginId = authenticationUserUtils.getCurrentUserId();
        Optional<User> isExistUser = userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        // flask 서버에서 받은 pef를 바탕으로 수치를 계산하고 최종 결과 반환
        return breathingService.calculateBreathingResult(todayBreathing, isExistUser.get());
    }

    @PutMapping("/modifyBreathing")
    public CustomAPIResponse<Map<String, Object>> modifyBreathing(
            @RequestPart("date")
            @NotEmpty(message = "날짜를 입력해주세요")
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "날짜 입력 형식을 맞춰주세요")
            String date,
            @RequestPart("firstFile") @NotNull(message = "첫번째 호흡 파일을 업로드 해주세요")
            MultipartFile firstFile,
            @RequestPart("secondFile") @NotNull(message = "두번째 호흡 파일을 업로드 해주세요")
            MultipartFile secondFile,
            @RequestPart("thirdFile") @NotNull(message = "세번째 호흡 파일을 업로드 해주세요")
            MultipartFile thirdFile) throws IOException{

        breathingCheckServiceImpl.deleteBreathing();

        // 오류로 인해 파일이 비어 있는지 수동으로 검증
        if (firstFile == null || firstFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "첫번째 호흡 파일을 업로드 해주세요");
        }
        if (secondFile == null || secondFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "두번째 호흡 파일을 업로드 해주세요");
        }
        if (thirdFile == null || thirdFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "세번째 호흡 파일을 업로드 해주세요");
        }

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
        Breathing todayBreathing = breathingCheckServiceImpl.analyzeBreathing(breathingUploadRequestDto, breathingFileId);

        // 현재 로그인된 사용자 정보 가져오기
        String loginId = authenticationUserUtils.getCurrentUserId();
        Optional<User> isExistUser = userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        // flask 서버에서 받은 pef를 바탕으로 수치를 계산하고 최종 결과 반환
        return breathingService.calculateBreathingResult(todayBreathing, isExistUser.get());

    }


}
