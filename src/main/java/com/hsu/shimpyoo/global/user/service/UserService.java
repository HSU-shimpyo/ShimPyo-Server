package com.hsu.shimpyoo.global.user.service;

import com.hsu.shimpyoo.global.entity.User;
import com.hsu.shimpyoo.global.jwt.security.JwtTokenProvider;
import com.hsu.shimpyoo.global.user.dto.SignUpDto;
import com.hsu.shimpyoo.global.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String signUp(SignUpDto dto) {

        // 이미 존재하는 아이디인지 확인
        if (userRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        // 비밀번호 암호화 및 User 엔티티로 변환
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));  // 비밀번호 암호화
        User user = User.toEntity(dto);

        // 사용자 저장
        userRepository.save(user);

        return "회원가입에 성공했습니다.";
    }
}
