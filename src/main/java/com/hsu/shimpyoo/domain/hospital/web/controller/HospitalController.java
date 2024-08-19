package com.hsu.shimpyoo.domain.hospital.web.controller;

import com.hsu.shimpyoo.domain.hospital.service.HospitalService;
import com.hsu.shimpyoo.domain.hospital.service.HospitalServiceImpl;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalSearchRequestDto;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalVisitSetRequestDto;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/hospital")
@RequiredArgsConstructor
public class HospitalController {
    private final HospitalService hospitalService;
    private static final Logger log = LoggerFactory.getLogger(HospitalServiceImpl.class);

    @PostMapping("/searchHospital")
    public ResponseEntity<CustomAPIResponse<?>> searchHospitals(@Valid @RequestBody HospitalSearchRequestDto hospitalSearchRequestDto){
       ResponseEntity<CustomAPIResponse<?>> result= hospitalService.searchHospital(hospitalSearchRequestDto);
       return result;
    }

    @PostMapping("/setVisitHospital")
    public ResponseEntity<CustomAPIResponse<?>> setVisitHospital(@Valid @RequestBody HospitalVisitSetRequestDto hospitalVisitSetRequestDto){
        LocalDateTime reservationDateTime = hospitalVisitSetRequestDto.timeFormat(); // 입력 받은 일정을 LocalDateTime 형식으로 변환

        // 방문 시간이 현재보다 미래인지 검증
        if (reservationDateTime.isBefore(LocalDateTime.now())) {
            CustomAPIResponse<Object> res = CustomAPIResponse.createFailWithout(400, "방문 일정은 현재보다 미래여야 합니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        ResponseEntity<CustomAPIResponse<?>> result=hospitalService.setVisitHospital(hospitalVisitSetRequestDto);
        return result;
    }
}
