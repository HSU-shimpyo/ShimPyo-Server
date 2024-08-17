package com.hsu.shimpyoo.domain.hospital.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.shimpyoo.domain.hospital.entity.Hospital;
import com.hsu.shimpyoo.domain.hospital.repository.HospitalRepository;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalRequestDto;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalResponseDto;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {
    private final HospitalRepository hospitalRepository;
    // RestTemplate : HTTP 통신을 위한 도구로 RESTful API 웹 서비스와의 상호작용을 쉽게
    // 외부 도메인에서 데이터를 가져오거나 전송할 때 사용되는 스프링 프레임워크의 클래스
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.api.key}")
    private String apiKey;

    // 병원 검색
    @Override
    public ResponseEntity<CustomAPIResponse<?>> searchHospital(HospitalRequestDto hospitalRequestDto) {
        // 병원 정보가 db에 없으면 병원 정보를 db에 삽입
        if(hospitalRepository.count()==0) {
            insertHospitalIntoDB();
        }

        // 키워드를 기반으로 병원을 검색
        List<Hospital> hospitals = hospitalRepository.findByHospitalNameContaining(hospitalRequestDto.getKeyword());

        if (hospitals.isEmpty()) { // 검색 결과가 없을 경우
            CustomAPIResponse<Object> res = CustomAPIResponse.createSuccess(200, null, "검색 결과가 없습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } else { // 검색 결과가 존재할 경우
            CustomAPIResponse<Object> res = CustomAPIResponse.createSuccess(200, hospitals, "조건에 맞는 검색 결과를 불러왔습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
    }

    // 병원 정보를 db에 삽입
    @Transactional
    public void insertHospitalIntoDB() {
        // HttpHeaers는 HTTP 요청의 헤더를 구성
        // -> 카카오 API 호출 시, 인증 정보를 헤더에 포함 시켜야 하므로
        // HttpHeaders 객체에 Authorization 헤더를 설정
        HttpHeaders headers = new HttpHeaders(); // 요청 매개변수 설정
        headers.setAccept(List.of(MediaType.APPLICATION_JSON)); // Accept 헤더 설정
        headers.setContentType(MediaType.APPLICATION_JSON); // Content Type 헤더 설정
        headers.set("Authorization", "KakaoAK " + apiKey);

        ObjectMapper objectMapper = new ObjectMapper(); // json 파싱

        int page = 1;
        int totalPages = 1; // 1로 초기화 후, 요청을 보내면 수정

        do {
            String baseUrl = "https://dapi.kakao.com/v2/local/search/keyword.json?" +
                    "query=호흡기 내과" +
                    "&category_group_code=HP8" + // 병원 코드는 HP8
                    "&sort=accuracy" +
                    "&page=" + page +
                    "&size=15"; // 정확도 순은 accuracy, 거리순은 distance

            HttpEntity<String> entity = new HttpEntity<>("", headers);
            // exchange() : 헤더를 생성하고, 모든 요청 방법을 허용 -> Http 요청 및 응답 처리
            ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String.class);


            // 성공 시 응답
            if (response.getStatusCode().is2xxSuccessful()) {
                try {
                    JsonNode root = objectMapper.readTree(response.getBody());
                    JsonNode documents = root.path("documents");
                    JsonNode meta = root.path("meta");

                    if (page == 1) {
                        int totalCount = meta.path("total_count").asInt();
                        totalPages = (totalCount + 14) / 15; // 전체 페이지 수 계산
                    }

                    // 전체 페이지 수 계산
                    List<Hospital> hospitals = new ArrayList<>();
                    for (JsonNode node : documents) {
                        String hospitalName = node.path("place_name").asText();
                        String hospitalAddress = node.path("road_address_name").asText();

                        // 이미 존재하는 병원인지 이름과 주소로 체크
                        if (hospitalRepository.findByHospitalNameAndHospitalAddress(hospitalName, hospitalAddress).isEmpty()) {
                            HospitalResponseDto hospitalResponseDto = HospitalResponseDto.builder()
                                    .hospitalName(hospitalName)
                                    .hospitalAddress(hospitalAddress)
                                    .hospitalPhone(node.path("phone").asText())
                                    .hospitalUrl(node.path("place_url").asText())
                                    .build();

                            Hospital hospital = Hospital.toEntity(hospitalResponseDto);
                            hospitals.add(hospital);
                        }
                    }

                    // 병원 정보 db에 저장
                    if (!hospitals.isEmpty()) {
                        hospitalRepository.saveAll(hospitals);
                    }
                    page++;

                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Json 파싱 중 오류가 발생했습니다.");
                }
            } else {
                // API 호출 실패 시
                throw new RuntimeException("카카오 API 호출에 실패했습니다.");
            }
        }while(page<=totalPages);
    }
}
