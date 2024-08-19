package com.hsu.shimpyoo.domain.hospital.service;

import com.hsu.shimpyoo.domain.hospital.entity.Hospital;
import com.hsu.shimpyoo.domain.hospital.entity.HospitalVisit;
import com.hsu.shimpyoo.domain.hospital.repository.HospitalRepository;
import com.hsu.shimpyoo.domain.hospital.repository.HospitalVisitRepository;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalRequestDto;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalVisitRequestDto;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {
    private static final Logger logger = LoggerFactory.getLogger(HospitalServiceImpl.class);
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final HospitalVisitRepository hospitalVisitRepository;

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

    @Override
    public ResponseEntity<CustomAPIResponse<?>> setVisitHospital(HospitalVisitRequestDto hospitalVisitRequestDto) {
        logger.info("Starting setVisitHospital method with request: {}", hospitalVisitRequestDto);
        // 현재 인증된 사용자의 로그인 아이디를 가져옴 (getName은 loginId를 가져오는 것)
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자 존재 여부 확인
        User isExistUser=userRepository.findByLoginId(loginId)
                .orElseThrow(()->new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        // 병원 존재 여부 확인
        Hospital isExistHospital=hospitalRepository.findById(hospitalVisitRequestDto.getHospitalId())
                .orElseThrow(()->new EntityNotFoundException("존재하지 않는 병원입니다."));

        HospitalVisit hospitalVisit=HospitalVisit.builder()
                .userId(isExistUser)
                .hospitalId(isExistHospital)
                .visitTime(hospitalVisitRequestDto.getReservationDateTime())
                .build();

        hospitalVisitRepository.save(hospitalVisit);

        CustomAPIResponse<Object> res = CustomAPIResponse.createSuccess(200, null, "병원 방문 일정이 기록되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


}
