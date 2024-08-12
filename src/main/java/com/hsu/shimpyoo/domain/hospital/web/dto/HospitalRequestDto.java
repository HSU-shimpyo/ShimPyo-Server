package com.hsu.shimpyoo.domain.hospital.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HospitalRequestDto {
    // 현재 위치를 바탕으로 주변 호흡기내과를 조회 및 페이징 처리

    // 현재 위치의 경도 (x)
    @NotNull(message="경도를 입력해주세요.")
    private double longitude;

    // 현재 위치의 위도 (y)
    @NotNull(message = "위도를 입력해주세요.")
    private double latitude;

    // 현재 페이지 번호 -> 기본 값을 1로 설정
    private int page = 1;

    // 한 페이지에 출력할 목록의 수 -> 기본 값을 10으로 설정
    private int size = 10;

    public HospitalRequestDto(double latitude, double longitude, int page, int size) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.page = page;
        this.size = size;
    }

}
