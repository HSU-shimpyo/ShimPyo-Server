package com.hsu.shimpyoo.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hsu.shimpyoo.domain.user.dto.SignUpDto;
import com.hsu.shimpyoo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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

    @Column(name = "name", nullable = false)
    private String name; // 이름

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId; // 아이디

    @Column(name = "password", nullable = false)
    private String password; // 비밀번호

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    @Column(name = "birth", nullable = false)
    private Date birth; // 생년월일

    @Column(name = "pef")
    private Float pef; // 최대호기량

    public static User toEntity(SignUpDto dto, String encryptedPassword) {

        User user = User.builder()
                .name(dto.getName())
                .loginId(dto.getLoginId())
                .password(encryptedPassword)
                .birth(dto.getBirth())
                .pef(Float.valueOf(dto.getPef()))
                .build();

        return user;
    }
}
