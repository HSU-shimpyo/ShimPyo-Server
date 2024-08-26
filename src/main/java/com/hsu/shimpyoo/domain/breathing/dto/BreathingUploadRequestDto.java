package com.hsu.shimpyoo.domain.breathing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BreathingUploadRequestDto {
    // 첫번째 호흡 파일
    private MultipartFile first_file;

    // 두번째 호흡 파일
    private MultipartFile second_file;

    // 세번째 호흡 파일
    private MultipartFile third_file;
}
