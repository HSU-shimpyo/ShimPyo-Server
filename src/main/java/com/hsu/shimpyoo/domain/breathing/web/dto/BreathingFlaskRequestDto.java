package com.hsu.shimpyoo.domain.breathing.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BreathingFlaskRequestDto {
    // 첫번째 측정 파일 url
    private String firstFile;

    // 두번째 측정 파일 url
    private String secondFile;

    // 세번째 측정 파일 url
    private String thirdFile;
}
