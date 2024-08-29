package com.hsu.shimpyoo.domain.breathing.controller;

import com.hsu.shimpyoo.domain.breathing.dto.BreathingUploadRequestDto;
import com.hsu.shimpyoo.domain.breathing.service.BreathingCheckServiceImpl;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/breathing/check")
@RequiredArgsConstructor
public class BreathingCheckController {
    private final BreathingCheckServiceImpl breathingCheckServiceImpl;

    // 녹음 파일 업로드
    @PostMapping("/uploadFile")
    public ResponseEntity<CustomAPIResponse<?>> uploadFile(
            @RequestPart("date") String date,
            @RequestPart("firstFile") MultipartFile firstFile,
            @RequestPart("secondFile") MultipartFile secondFile,
            @RequestPart("thirdFile") MultipartFile thirdFile) throws IOException {

        // 오류로 인해 RequestPart로 받은 후, DTO로 변환
        BreathingUploadRequestDto breathingUploadRequestDto = new BreathingUploadRequestDto(date, firstFile, secondFile, thirdFile);

        ResponseEntity<CustomAPIResponse<?>> result=breathingCheckServiceImpl.uploadBreathing(breathingUploadRequestDto);
        return result;
    }
}
