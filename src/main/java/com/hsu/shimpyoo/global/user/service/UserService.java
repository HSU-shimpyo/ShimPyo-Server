package com.hsu.shimpyoo.global.user.service;

import com.hsu.shimpyoo.global.entity.User;
import com.hsu.shimpyoo.global.jwt.security.JwtTokenProvider;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import com.hsu.shimpyoo.global.user.dto.SignInReqDto;
import com.hsu.shimpyoo.global.user.dto.SignInResDto;
import com.hsu.shimpyoo.global.user.dto.SignUpDto;
import com.hsu.shimpyoo.global.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ResponseEntity<CustomAPIResponse<Map<String, String>>> signUp(SignUpDto dto) {
        if (userRepository.findByUserId(dto.getUserId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    CustomAPIResponse.createFailWithout(409, "이미 존재하는 아이디입니다.")
            );
        }

        String encryptedPassword = passwordEncoder.encode(dto.getPassword());
        User user = User.toEntity(dto, encryptedPassword);
        userRepository.save(user);

        // Access token과 Refresh token 생성
        String accessToken = jwtTokenProvider.createToken(user.getUserId());

        // 응답 데이터 생성
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);

        // 성공 응답 생성
        CustomAPIResponse<Map<String, String>> response = CustomAPIResponse.createSuccess(200, tokens, "회원가입에 성공했습니다.");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    public SignInResDto signIn(SignInReqDto dto) {
        User user = userRepository.findByUserId(dto.getUserId()).orElseThrow(RuntimeException::new);

        String token = jwtTokenProvider.createToken(user.getUserId());
        return SignInResDto.builder().accessToken(token).build();
    }
}

