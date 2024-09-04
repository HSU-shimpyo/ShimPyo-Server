package com.hsu.shimpyoo.domain.hospital.service;

import com.hsu.shimpyoo.domain.hospital.entity.Hospital;
import com.hsu.shimpyoo.domain.hospital.entity.HospitalVisit;
import com.hsu.shimpyoo.domain.hospital.repository.HospitalRepository;
import com.hsu.shimpyoo.domain.hospital.repository.HospitalVisitRepository;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalSearchRequestDto;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalSearchResponseDto;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalVisitDto;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // 병원 방문 일정 설정
    @Transactional
    @Override
    public ResponseEntity<CustomAPIResponse<?>> setVisitHospital(HospitalVisitSetRequestDto hospitalVisitSetRequestDto) {
        // 현재 인증된 사용자의 로그인 아이디를 가져옴 (getName은 loginId를 가져오는 것)
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자 존재 여부 확인
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        // 병원 존재 여부 확인
        Optional<Hospital> isExistHospital=hospitalRepository.findById(hospitalVisitSetRequestDto.getHospitalId());
        if(isExistHospital.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 병원입니다.");
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

    // 병원 방문 일정 전체 조회
    @Override
    public ResponseEntity<CustomAPIResponse<?>> getAllHospitalVisit() {
        // 사용자 정보를 가져온다
        Optional<User> isExistUser=userRepository.findByLoginId(SecurityContextHolder.getContext().getAuthentication().getName());
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        // 병원 방문 기록 모두 조회
        List<HospitalVisitDto> hospitalVisitDtoList=hospitalVisitRepository.findByUserId(isExistUser.get())
                .stream()
                .map(hospitalVisit -> HospitalVisitDto.builder()
                        .hospitalVisitId(hospitalVisit.getHospitalVisitId())
                        .hospitalName(hospitalVisit.getHospitalId().getHospitalName())
                        .hospitalAddress(hospitalVisit.getHospitalId().getHospitalAddress())
                        .hospitalPhoneNumber(hospitalVisit.getHospitalId().getHospitalPhone())
                        .visitTime(hospitalVisit.getVisitTime())
                        .build())
                .toList();

        CustomAPIResponse<Object> res=CustomAPIResponse.createSuccess(200, hospitalVisitDtoList, "병원 방문 일정이 조회되었습니다.");

        if(hospitalVisitDtoList.isEmpty()){
            res=CustomAPIResponse.createSuccess(200, null, "아직 방문 일정을 설정하지 않았습니다.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


}
