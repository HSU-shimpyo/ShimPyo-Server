package com.hsu.shimpyoo.domain.breathing.entity;

import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BREATHING_FILE")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreathingFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "breathing_file_id")
    private Long breathingFileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId; // 사용자 기본키

    @Column(name="first_url")
    private String firstUrl; // 첫번째 파일의 url

    @Column(name="second_url")
    private String secondUrl; // 두번째 파일의 url

    @Column(name="third_url")
    private String thirdUrl; // 세번째 파일의 url
}
