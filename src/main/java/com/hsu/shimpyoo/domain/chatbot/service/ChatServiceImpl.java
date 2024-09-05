package com.hsu.shimpyoo.domain.chatbot.service;

import com.hsu.shimpyoo.domain.chatbot.dto.ChatMessageDto;
import com.hsu.shimpyoo.domain.chatbot.dto.ChatRequestDto;
import com.hsu.shimpyoo.domain.chatbot.dto.ChatResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {
    private final RestTemplate restTemplate;
    private final String apiUrl = "https://api.openai.com/v1/chat/completions";
    private final String promptPrefix = "너는 지상 최고의 천식 전문가야. 최선을 다 해서 천식에 관한 답을 해줘." +
            " 천식에 대한 답변이 아니면, " + "'저는 천식 관련 챗봇이에요, 천식과 관련된 질문만 답변드릴 수 있습니다.'는 답변을 해줘.";

    @Override
    public ChatResponseDto askForChat(String content) {
        // 사용자가 보낸 메시지에 프롬프트를 더하여 새로운 메시지를 생성
        String realMessage = addPrefixToUserMessage(content);

        // ChatRequestDto 객체를 생성하여 OpenAI API에 사용할 모델 및 메시지 추가
        ChatRequestDto realRequest = new ChatRequestDto("gpt-3.5-turbo", realMessage);

        String responseMessage = generateResponse(realRequest);

        return createResponse(responseMessage);
    }

    // 사용자 메시지에 프롬프트를 추가하는 메서드
    private String addPrefixToUserMessage(String userMessage) {
        return promptPrefix + " " + userMessage;
    }

    // openAI api 호출
    private String generateResponse(ChatRequestDto request) {
        // 1. 요청 엔티티 생성 (이미 RestTemplate에 API Key가 설정되어 있음)
        HttpEntity<ChatRequestDto> entity = new HttpEntity<>(request);

        try {
            // 2. RestTemplate을 사용하여 OpenAI API 호출
            ResponseEntity<ChatResponseDto> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    ChatResponseDto.class
            );

            // 3. 응답이 존재하고, 유효한지 확인 후 메시지 반환
            if (response.getBody() != null && !response.getBody().getChatChoices().isEmpty()) {
                return response.getBody().getChatChoices().get(0).getChatMessageDto().getContent();
            } else {
                throw new IllegalStateException("No valid response from OpenAI API.");
            }
        } catch (Exception e) {
            // 예외 발생 시 처리 (로그 출력 등)
            throw new RuntimeException("Error occurred while communicating with OpenAI API: " + e.getMessage(), e);
        }
    }

    // 응답을 생성
    private ChatResponseDto createResponse(String responseMessage) {
        ChatMessageDto chatMessageDto = new ChatMessageDto("assistant", responseMessage);
        // ChatChoice 객체 생성 후 필드 설정
        ChatResponseDto.ChatChoice chatChoice = new ChatResponseDto.ChatChoice();
        chatChoice.setIndex(0); // 인덱스 설정
        chatChoice.setChatMessageDto(chatMessageDto); // 메시지 DTO 설정

        List<ChatResponseDto.ChatChoice> chatChoices = new ArrayList<>();
        chatChoices.add(chatChoice);

        return new ChatResponseDto(chatChoices);
    }


}