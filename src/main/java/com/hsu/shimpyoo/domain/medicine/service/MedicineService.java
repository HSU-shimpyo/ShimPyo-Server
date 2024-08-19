package com.hsu.shimpyoo.domain.medicine.service;

import com.hsu.shimpyoo.domain.medicine.dto.MedicineRequestDto;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

public interface MedicineService {
    ResponseEntity<CustomAPIResponse<?>> MedicineTimeSetting(MedicineRequestDto dto);
}
