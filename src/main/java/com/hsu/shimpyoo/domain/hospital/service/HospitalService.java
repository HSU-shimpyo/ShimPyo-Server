package com.hsu.shimpyoo.domain.hospital.service;

import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalSearchRequestDto;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalVisitSetRequestDto;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

public interface HospitalService {
    ResponseEntity<CustomAPIResponse<?>> searchHospital(HospitalSearchRequestDto hospitalSearchRequestDto);
    ResponseEntity<CustomAPIResponse<?>> setVisitHospital(HospitalVisitSetRequestDto hospitalVisitSetRequestDto);
    ResponseEntity<CustomAPIResponse<?>> getAllHospitalVisit();
}
