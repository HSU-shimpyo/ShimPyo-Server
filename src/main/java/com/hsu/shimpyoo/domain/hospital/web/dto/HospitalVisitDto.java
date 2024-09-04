package com.hsu.shimpyoo.domain.hospital.web.dto;

import com.hsu.shimpyoo.domain.hospital.entity.Hospital;
import com.hsu.shimpyoo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HospitalVisitDto {
    // 병원 방문 기본 키
    private Long hospitalVisitId;

    // 방문할 병원 이름
    private String hospitalName;

    // 방문할 병원 주소
    private String hospitalAddress;

    // 방문할 병원의 연락처
    private String hospitalPhoneNumber;

    // 방문할 시각
    private LocalDateTime visitTime;
}
