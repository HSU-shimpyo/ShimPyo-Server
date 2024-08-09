package com.hsu.shimpyoo.global.entity;

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

    @Column(name = "password", nullable = false)
    private String password; // 비밀번호

    @Column(name = "birth", nullable = false)
    private Date birth; // 생년월일
}
