package com.hsu.shimpyoo.domain.chatbot.service;

import com.hsu.shimpyoo.domain.chatbot.entity.ChatRoom;
import com.hsu.shimpyoo.domain.chatbot.repository.ChatRoomRepository;
import com.hsu.shimpyoo.domain.chatbot.web.dto.ModifyChatRoomTitleDto;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // 채팅방 생성
    @Override
    public ResponseEntity<CustomAPIResponse<?>> makeChatRoom(){
        // 현재 인증된 사용자의 로그인 아이디를 가져옴 (getName은 loginId를 가져오는 것)
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자 존재 여부 확인
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        ChatRoom chatRoom = ChatRoom.
                builder()
                .chatTitle("채팅방")
                .userId(isExistUser.get())
                .build();

        chatRoomRepository.save(chatRoom);

        CustomAPIResponse<Object> res=CustomAPIResponse.createSuccess(200,  null, "채팅방이 생성되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 채팅방 제목 수정
    @Override
    public ResponseEntity<CustomAPIResponse<?>> modifyChatRoomTitle(ModifyChatRoomTitleDto requestDto) {
        // 현재 인증된 사용자의 로그인 아이디를 가져옴 (getName은 loginId를 가져오는 것)
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자 존재 여부 확인
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        Optional<ChatRoom> isExistChatRoom = chatRoomRepository.findById(requestDto.getChatRoomId());

        if(isExistChatRoom.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 채팅방입니다.");
        }

        // 채팅방이 현재 로그인한 사용자의 채팅방이 아니라면
        if(isExistChatRoom.get().getUserId()!=isExistUser.get()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"채팅방 제목 수정 권한이 없습니다.");
        }

        isExistChatRoom.get().setChatTitle(requestDto.getTitle());
        chatRoomRepository.save(isExistChatRoom.get());

        CustomAPIResponse<Object> res = CustomAPIResponse.createSuccess(200, null ,
                "채팅방 제목이 수정되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 채팅방 목록 조회
    @Override
    public ResponseEntity<CustomAPIResponse<?>> getAllChatRooms() {
        // 현재 인증된 사용자의 로그인 아이디를 가져옴 (getName은 loginId를 가져오는 것)
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자 존재 여부 확인
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        List<ChatRoom> chatRoomList= chatRoomRepository.findChatRoomByUserId(isExistUser.get());

        return null;
    }
}
