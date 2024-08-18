package com.hsu.shimpyoo.domain.hospital.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.shimpyoo.domain.hospital.entity.Hospital;
import com.hsu.shimpyoo.domain.hospital.repository.HospitalRepository;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalRequestDto;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalResponseDto;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalVisitRequestDto;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import com.hsu.shimpyoo.global.security.service.MyUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;

    // 병원 검색
    @Override
    public ResponseEntity<CustomAPIResponse<?>> searchHospital(HospitalRequestDto hospitalRequestDto) {
        // 키워드를 기반으로 병원을 검색
        List<Hospital> hospitals = hospitalRepository.findByHospitalNameContaining(hospitalRequestDto.getKeyword());

        if (hospitals.isEmpty()) { // 검색 결과가 없을 경우
            CustomAPIResponse<Object> res = CustomAPIResponse.createFailWithout(404, "검색 결과가 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        } else { // 검색 결과가 존재할 경우
            CustomAPIResponse<Object> res = CustomAPIResponse.createSuccess(200, hospitals, "조건에 맞는 검색 결과를 불러왔습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
    }


}
