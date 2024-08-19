package com.hsu.shimpyoo.domain.hospital.service;

import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalRequestDto;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalVisitRequestDto;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

public interface HospitalService {
    ResponseEntity<CustomAPIResponse<?>> searchHospital(HospitalRequestDto hospitalRequestDto);
    ResponseEntity<CustomAPIResponse<?>> setVisitHospital(HospitalVisitRequestDto hospitalVisitRequestDto);
}
