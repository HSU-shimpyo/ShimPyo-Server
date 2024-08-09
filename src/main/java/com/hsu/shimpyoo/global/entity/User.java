package com.hsu.shimpyoo.global.entity;

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
    @Column(name = "user_id")
    private Long userId; // 사용자 기본 키

    @Column(name = "id", nullable = false, unique = true)
    private String id; // 아이디

    @Column(name = "password", nullable = false)
    private String password; // 비밀번호

    @Column(name = "birth", nullable = false)
    private Date birth; // 생년월일
}
