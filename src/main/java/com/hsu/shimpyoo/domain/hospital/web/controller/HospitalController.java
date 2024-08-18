package com.hsu.shimpyoo.domain.hospital.web.controller;

import com.hsu.shimpyoo.domain.hospital.service.HospitalService;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalRequestDto;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospital")
@RequiredArgsConstructor
public class HospitalController {
    private final HospitalService hospitalService;

    @PostMapping("/searchHospital")
    public ResponseEntity<CustomAPIResponse<?>> searchHospitals(@Valid @RequestBody HospitalRequestDto hospitalRequestDto){
       ResponseEntity<CustomAPIResponse<?>> result= hospitalService.searchHospital(hospitalRequestDto);
       return result;
    }
}
