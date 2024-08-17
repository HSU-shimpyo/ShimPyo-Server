package com.hsu.shimpyoo.domain.hospital.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HospitalRequestDto {
    // 호흡기내과를 조회 및 페이징 처리

    // 현재 페이지 번호 -> 기본 값을 1로 설정
    private int page = 1;

    // 한 페이지에 출력할 목록의 수 -> 기본 값을 10으로 설정
    private int size = 10;

    public HospitalRequestDto() {}

    public HospitalRequestDto(int page, int size) {
        this.page = page;
        this.size = size;
    }

}
