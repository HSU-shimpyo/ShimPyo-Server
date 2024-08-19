package com.hsu.shimpyoo.domain.hospital.entity;

import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "HOSPITAL_VISIT")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalVisit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hospital_visit_id")
    private Long hospitalVisitId; // 병원 방문 기본키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospitalId;

    @Column(name = "visit_time", nullable = false)
    private LocalDateTime visitTime;

}
