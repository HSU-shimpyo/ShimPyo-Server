package com.hsu.shimpyoo.domain.hospital.service;

import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

public interface HospitalService {
    public ResponseEntity<CustomAPIResponse<?>> searchHospital();
}
