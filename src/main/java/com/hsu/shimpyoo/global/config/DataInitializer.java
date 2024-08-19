package com.hsu.shimpyoo.global.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.shimpyoo.domain.hospital.entity.Hospital;
import com.hsu.shimpyoo.domain.hospital.repository.HospitalRepository;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final HospitalRepository hospitalRepository;

    // RestTemplate : HTTP 통신을 위한 도구로 RESTful API 웹 서비스와의 상호작용을 쉽게
    // 외부 도메인에서 데이터를 가져오거나 전송할 때 사용되는 스프링 프레임워크의 클래스
    private final RestTemplate restTemplate=new RestTemplate();


    @Value("${kakao.api.key}")
    private String apiKey;

    @Override
    public void run(String... args) throws Exception {
        if(hospitalRepository.count()==0) {
            // 삽입하려는 병원 키워드
            List<String> keywords = List.of("호흡기내과", "알레르기", "천식", "기관지", "호흡기", "폐질환", "대학병원", "종합병원");


            // 중복된 병원 이름과 주소를 담을 Set
            Set<String> existingHospitalKeys = new HashSet<>();

            for (String keyword : keywords) {
                List<Hospital> hospitalsToInsert = getAllHospital(keyword);

                // 가져온 병원 정보를 필터링
                List<Hospital> filteredHospitals = hospitalsToInsert.stream()
                        .filter(hospital -> {
                            String key = hospital.getHospitalName();
                            // 병원 정보가 이미 존재하는지
                            if (existingHospitalKeys.contains(key)) {
                                return false;
                            }
                            existingHospitalKeys.add(key);
                            return true;
                        })
                        .collect(Collectors.toList());

                // 필터링된 병원 정보만 DB에 저장
                if (!filteredHospitals.isEmpty()) {
                    hospitalRepository.saveAll(filteredHospitals);
                }
            }
            System.out.println("병원 정보가 데이터베이스에 삽입되었습니다.");
        }
    }

    // 병원 정보를 db에 삽입
    public List<Hospital> getAllHospital(String keyword) {
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
        List<Hospital> hospitals = new ArrayList<>();

        do {
            String baseUrl = "https://dapi.kakao.com/v2/local/search/keyword.json?" +
                    "query="+keyword+ // 검색할 항목
                    "&category_group_code=HP8" + // 병원 코드는 HP8
                    "&sort=accuracy" + // 정확도 순은 accuracy, 거리순은 distance
                    "&page=" + page +
                    "&size=15";


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
                        totalPages = (totalCount + 14) / 15;
                    }

                    for (JsonNode node : documents) {
                        String hospitalName = node.path("place_name").asText();
                        String hospitalAddress = node.path("road_address_name").asText();

                        HospitalResponseDto hospitalResponseDto = HospitalResponseDto.builder()
                                .hospitalName(hospitalName)
                                .hospitalAddress(hospitalAddress)
                                .hospitalPhone(node.path("phone").asText())
                                .build();

                        Hospital hospital = Hospital.toEntity(hospitalResponseDto);
                        hospitals.add(hospital);

                    }

                    page++;

                } catch (JsonProcessingException e) {
                    System.err.println("JSON 파싱 중 오류가 발생했습니다: " + e.getMessage());
                }
            } else {
                System.err.println("카카오 API 호출에 실패했습니다. 상태 코드: " + response.getStatusCode());
            }
        } while (page <= totalPages);

        return hospitals;
    }
}
