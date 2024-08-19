package com.hsu.shimpyoo.domain.hospital.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class HospitalVisitSetRequestDto {
    @NotNull(message = "병원 기본키를 입력해주세요.")
    private Long hospitalId; // 병원 기본키


    @NotNull(message = "방문 날짜와 시간을 입력해주세요.")
    private String reservationDateTime; // 병원 방문 시간

    public LocalDateTime timeFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 H시 m분");
        return LocalDateTime.parse(this.reservationDateTime, formatter);
    }
}
