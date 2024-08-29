package com.hsu.shimpyoo.domain.breathing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreathingUploadRequestDto {
    // 측정 날짜
    @NotNull(message = "측정 날짜를 입력해주세요")
    private String date;

    // 첫번째 파일
    @NotNull(message = "호흡 파일을 삽입해주세요")
    private MultipartFile firstFile;

    // 두번째 파일
    @NotNull(message = "호흡 파일을 삽입해주세요")
    private MultipartFile secondFile;

    // 세번째 파일
    @NotNull(message = "호흡 파일을 삽입해주세요")
    private MultipartFile thirdFile;
}
