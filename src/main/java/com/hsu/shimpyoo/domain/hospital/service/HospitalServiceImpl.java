package com.hsu.shimpyoo.domain.hospital.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalRequestDto;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalResponseDto;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
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

    @Value("${kakao.api.key}")
    private String apiKey;

    @Override
    public ResponseEntity<CustomAPIResponse<?>> searchHospital(HospitalRequestDto hospitalRequestDto) {

        // HttpHeaers는 HTTP 요청의 헤더를 구성
        // -> 카카오 API 호출 시, 인증 정보를 헤더에 포함 시켜야 하므로
        // HttpHeaders 객체에 Authorization 헤더를 설정
        HttpHeaders headers = new HttpHeaders(); // 요청 매개변수 설정
        headers.setAccept(List.of(MediaType.APPLICATION_JSON)); // Accept 헤더 설정
        headers.setContentType(MediaType.APPLICATION_JSON); // Content Type 헤더 설정
        headers.set("Authorization", "KakaoAK "+apiKey);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        String baseUrl = "https://dapi.kakao.com/v2/local/search/keyword.json?" +
                "query=호흡기내과" +
                "&category_group_code=HP8" + // 병원 코드는 HP8
                "&page=" + hospitalRequestDto.getPage() +
                "&size="+ hospitalRequestDto.getSize() +
                "&sort=accuracy"; // 정확도 순은 accuracy, 거리순은 distance

        // RestTemplate : HTTP 통신을 위한 도구로 RESTful API 웹 서비스와의 상호작용을 쉽게
        // 외부 도메인에서 데이터를 가져오거나 전송할 때 사용되는 스프링 프레임워크의 클래스
        RestTemplate restTemplate = new RestTemplate();

        // exchange() : 헤더를 생성하고, 모든 요청 방법을 허용 -> Http 요청 및 응답 처리
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                entity,
                String.class
        );

        // 성공 시 응답
        if (response.getStatusCode().is2xxSuccessful()) {
            ObjectMapper objectMapper = new ObjectMapper(); // Json 파싱
            JsonNode root = null;
            try {
                root = objectMapper.readTree(response.getBody());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Json 파싱 중 오류가 발생했습니다.");
            }
            JsonNode documents = root.path("documents");

            List<HospitalResponseDto> hospitalList = new ArrayList<>();
            for (JsonNode node : documents) {
                HospitalResponseDto hospital = new HospitalResponseDto();
                hospital.setHospitalName(node.path("place_name").asText());
                hospital.setHospitalAddress(node.path("road_address_name").asText());
                hospital.setHospitalPhone(node.path("phone").asText());
                hospital.setHospitalUrl(node.path("place_url").asText());
                hospitalList.add(hospital);
            }


            CustomAPIResponse<List<HospitalResponseDto>> customResponse = CustomAPIResponse.createSuccess(
                    HttpStatus.OK.value(), // status
                    hospitalList, // data
                    "호흡기내과 목록 검색에 성공했습니다." // message
            );
            return ResponseEntity.ok(customResponse);

        } else {
            // 실패 시 응답
            CustomAPIResponse<String> customResponse = CustomAPIResponse.createFailWithout(
                    response.getStatusCode().value(), // status
                    "호흡기내과 목록 검색에 실패했습니다." // message
            );
            return ResponseEntity.status(response.getStatusCode()).body(customResponse);
        }
    }
}
