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
    public ChatResponseDto askForChat(ChatRequestDto chatRequestDto) {
        String userMessage = getUserMessageFromRequest(chatRequestDto);
        String realMessage = addPrefixToUserMessage(userMessage);

        ChatRequestDto realRequest = new ChatRequestDto(chatRequestDto.getModel(), realMessage);
        String responseMessage = generateResponse(realRequest);

        return createResponse(responseMessage);
    }

    // 사용자 메시지에 프롬프트를 추가하는 메서드
    private String addPrefixToUserMessage(String userMessage) {
        return promptPrefix + " " + userMessage;
    }

    // ChatRequestDto에서 사용자 메시지를 가져옴
    private String getUserMessageFromRequest(ChatRequestDto request) {
        return request.getChatMessageDtos().stream()
                .filter(msg -> "user".equals(msg.getRole()))
                .map(ChatMessageDto::getContent)
                .findFirst()
                .orElse("");
    }

    // openAI api 호출
    private String generateResponse(ChatRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<ChatRequestDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ChatResponseDto> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                ChatResponseDto.class
        );

        // 응답 메시지 추출
        return Objects.requireNonNull(response.getBody()).getChatChoices().get(0).getChatMessageDto().getContent();
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