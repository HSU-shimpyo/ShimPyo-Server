package com.hsu.shimpyoo.domain.breathing.repository;

import com.hsu.shimpyoo.domain.breathing.entity.Breathing;
import com.hsu.shimpyoo.domain.breathing.entity.BreathingFile;
import com.hsu.shimpyoo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BreathingRepository extends JpaRepository<Breathing, Long> {
    // UserId로 가장 최근의 Breathing 데이터를 가져오는 메서드
    Breathing findTopByUserIdOrderByCreatedAtDesc(User user);

    // 지난 7일간 내역 조회
    Optional<Breathing> findTopByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(User user, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
