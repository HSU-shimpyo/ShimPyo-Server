package com.hsu.shimpyoo.domain.chatbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hsu.shimpyoo.domain.chatbot.entity.ChatRoom;
import com.hsu.shimpyoo.domain.chatbot.repository.ChatRepository;
import com.hsu.shimpyoo.domain.chatbot.repository.ChattingRoomRepository;
import com.hsu.shimpyoo.domain.chatbot.web.dto.ChatRequestDto;
import com.hsu.shimpyoo.domain.chatbot.web.dto.ModifyChatRoomTitleDto;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final String apiUrl = "https://api.openai.com/v1/chat/completions";

    @Value("${gpt.api.key}")
    private String apiKey;


    public ResponseEntity<CustomAPIResponse<?>> askForChat(String question) {
        try {
            String model = "gpt-3.5-turbo";  // 모델 명
            String role = "user";  // 역할

            // 요청 메시지 구성
            String prompt = "당신은 지상 최고의 천식 전문가입니다. 최선을 다 해서 천식에 관한 답을 해주세요." +
                    "답변은 300자 이내로 부탁드립니다. 천식과 관련된 질문은 반드시 답해야만 합니다." +
                    "천식에 대한 질문이 아니라서 답변을 할 수 없는 경우에만 '저는 천식 관련 챗봇이에요, 천식과 관련된 질문만 답변드릴 수 있습니다.'" +
                    "는 답변을 해주길 바랍니다."+"'네, 알겠습니다'와 같은 응답은 불필요합니다."+"질문은 다음과 같습니다.";

            ChatRequestDto requestDto = new ChatRequestDto(model, prompt+question);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 요청 바디를 JSON으로 직렬화
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(requestDto);

            // HTTP 엔티티 생성
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // RestTemplate을 통한 API 호출
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            // 응답 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                // 응답 본문에서 content만 추출
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                String content = jsonResponse.path("choices").get(0).path("message").path("content").asText();

                CustomAPIResponse<Object> res=CustomAPIResponse.createSuccess(200, content, "성공적으로 답변이 제공되었습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(res);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"API 호출 실패 : " + response.getStatusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"API 호출 중 오류 발생 : " + e.getMessage());
        }
    }
}
