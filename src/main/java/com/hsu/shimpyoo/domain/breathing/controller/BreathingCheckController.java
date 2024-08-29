package com.hsu.shimpyoo.domain.breathing.controller;

import com.hsu.shimpyoo.domain.breathing.dto.BreathingFlaskDto;
import com.hsu.shimpyoo.domain.breathing.dto.BreathingUploadRequestDto;
import com.hsu.shimpyoo.domain.breathing.entity.BreathingFile;
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
    public ResponseEntity<CustomAPIResponse<?>> getPef(
            @RequestPart("date") String date,
            @RequestPart("firstFile") MultipartFile firstFile,
            @RequestPart("secondFile") MultipartFile secondFile,
            @RequestPart("thirdFile") MultipartFile thirdFile) throws IOException {

        // 오류로 인해 RequestPart로 받은 후, DTO로 변환
        BreathingUploadRequestDto breathingUploadRequestDto = new BreathingUploadRequestDto(date, firstFile, secondFile, thirdFile);
        BreathingFile breathingFile = breathingCheckServiceImpl.uploadBreathing(breathingUploadRequestDto);

        BreathingFlaskDto breathingFlaskDto=new BreathingFlaskDto();

        // 플라스크 서버에 보낼 파일 url
        breathingFlaskDto.setFirstFile(breathingFile.getFirstUrl());
        breathingFlaskDto.setSecondFile(breathingFile.getSecondUrl());
        breathingFlaskDto.setThirdFile(breathingFile.getThirdUrl());

        // flask 서버로 파일 URL을 전송하고, PEF 값을 받아옴
        ResponseEntity<CustomAPIResponse<?>> response = breathingCheckServiceImpl.analyzeBreathing(breathingFlaskDto);

        // 최종적으로 PEF 값을 반환
        return response;
    }
}
