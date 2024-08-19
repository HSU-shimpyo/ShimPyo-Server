package com.hsu.shimpyoo.domain.medicine.controller;

import com.hsu.shimpyoo.domain.medicine.dto.MedicineRequestDto;
import com.hsu.shimpyoo.domain.medicine.service.MedicineService;
import com.hsu.shimpyoo.domain.medicine.service.MedicineServiceImpl;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medicine")
@RequiredArgsConstructor
public class MedicineController {
    private final MedicineServiceImpl medicineService;

    @PostMapping("/timeSetting")
    public ResponseEntity<CustomAPIResponse<?>> setMedicineTime(@RequestBody MedicineRequestDto dto) {
        // 약 복용 알림 시간 설정
        return medicineService.MedicineTimeSetting(dto);
    }
}
