package com.hsu.shimpyoo.domain.hospital.service;

import com.hsu.shimpyoo.domain.hospital.entity.Hospital;
import com.hsu.shimpyoo.domain.hospital.entity.HospitalVisit;
import com.hsu.shimpyoo.domain.hospital.repository.HospitalRepository;
import com.hsu.shimpyoo.domain.hospital.repository.HospitalVisitRepository;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalSearchRequestDto;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalSearchResponseDto;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalVisitSetRequestDto;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {
    private static final Logger logger = LoggerFactory.getLogger(HospitalServiceImpl.class);
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final HospitalVisitRepository hospitalVisitRepository;

    // 병원 검색
    @Override
    public ResponseEntity<CustomAPIResponse<?>> searchHospital(HospitalSearchRequestDto hospitalSearchRequestDto) {
        // 키워드를 기반으로 병원을 검색
        List<Hospital> hospitals = hospitalRepository.findByHospitalNameContaining(hospitalSearchRequestDto.getKeyword());

        if (hospitals.isEmpty()) { // 검색 결과가 없을 경우
            CustomAPIResponse<Object> res = CustomAPIResponse.createFailWithout(404, "검색 결과가 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        } else { // 검색 결과가 존재할 경우
            List<HospitalSearchResponseDto> hospitalSearchResponseDto = hospitals.stream()
                    .map(hospital -> HospitalSearchResponseDto.builder()
                            .hospitalId(hospital.getHospitalId()) // 병원 ID
                            .hospitalName(hospital.getHospitalName()) // 병원 이름
                            .hospitalAddress(hospital.getHospitalAddress()) // 병원 주소
                            .hospitalPhone(hospital.getHospitalPhone()) // 병원 전화번호
                            .build())
                    .toList();
            CustomAPIResponse<Object> res = CustomAPIResponse.createSuccess(200, hospitalSearchResponseDto ,
                    "조건에 맞는 검색 결과를 불러왔습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
    }

    @Override
    public ResponseEntity<CustomAPIResponse<?>> setVisitHospital(HospitalVisitSetRequestDto hospitalVisitSetRequestDto) {
        logger.info("Starting setVisitHospital method with request: {}", hospitalVisitSetRequestDto);
        // 현재 인증된 사용자의 로그인 아이디를 가져옴 (getName은 loginId를 가져오는 것)
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자 존재 여부 확인
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            CustomAPIResponse<Object> res=CustomAPIResponse.createFailWithout(404, "사용자를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }


        // 병원 존재 여부 확인
        Optional<Hospital> isExistHospital=hospitalRepository.findById(hospitalVisitSetRequestDto.getHospitalId());
        if(isExistHospital.isEmpty()){
            CustomAPIResponse<Object> res=CustomAPIResponse.createFailWithout(404, "존재하지 않는 병원입니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        HospitalVisit hospitalVisit=HospitalVisit.builder()
                .userId(isExistUser.get())
                .hospitalId(isExistHospital.get())
                .visitTime(hospitalVisitSetRequestDto.timeFormat())
                .build();

        hospitalVisitRepository.save(hospitalVisit);

        CustomAPIResponse<Object> res = CustomAPIResponse.createSuccess(200, null, "병원 방문 일정이 기록되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


}
