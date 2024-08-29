package com.hsu.shimpyoo.domain.breathing.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BreathingFlaskDto {
    // 첫번째 측정 파일 url
    private String firstFile;

    // 두번째 측정 파일 url
    private String secondFile;

    // 세번째 측정 파일 url
    private String thirdFile;
}
