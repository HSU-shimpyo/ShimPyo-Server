package com.hsu.shimpyoo.domain.hospital.web.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class HospitalVisitRequestDto {
    // 병원 기본키
    @NotNull(message = "병원 기본키를 입력해주세요.")
    private String hospitalId;

    // 방문 날짜 및 시간
    @NotNull(message = "예약 날짜와 시간을 입력해주세요.")
    @Future(message = "예약 날짜 및 시간은 현재보다 미래여야 합니다.")
    private LocalDateTime reservationDateTime;
}
