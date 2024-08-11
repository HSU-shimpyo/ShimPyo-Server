package com.hsu.shimpyoo.global.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hsu.shimpyoo.global.user.dto.SignUpDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "USER")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // 사용자 기본 키

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId; // 아이디

    private String password; // 비밀번호

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    @Column(name = "birth", nullable = false)
    private Date birth; // 생년월일

    private String role; // 사용자 권한

    public static User toEntity(SignUpDto dto, String encryptedPassword) {

        User user = User.builder()
                .userId(dto.getUserId())
                .password(encryptedPassword)
                .birth(dto.getBirth())
                .build();

        return user;
    }
}
