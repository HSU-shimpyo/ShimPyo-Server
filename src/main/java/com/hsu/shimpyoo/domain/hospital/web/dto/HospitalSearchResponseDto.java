package com.hsu.shimpyoo.domain.hospital.web.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HospitalSearchResponseDto {
    private Long hospitalId; // 병원 기본 키

    private String hospitalName; // 병원 이름

    private String hospitalAddress; // 병원 주소

    private String hospitalPhone; // 병원 전화번호

}
