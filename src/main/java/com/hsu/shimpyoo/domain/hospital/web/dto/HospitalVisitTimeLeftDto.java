package com.hsu.shimpyoo.domain.hospital.web.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HospitalVisitTimeLeftDto {
    // 며칠 남았는지
    private int day;

    // 몇시간 남았는지
    private int hour;

    // 몇분 남았는지
    private int minute;

}
