package com.hsu.shimpyoo.domain.breathing.repository;

import com.hsu.shimpyoo.domain.breathing.entity.DailyPef;
import com.hsu.shimpyoo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DailyPefRepository extends JpaRepository<DailyPef, Long> {
    Optional<DailyPef> findTopByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(User userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<DailyPef> findAllByUserIdAndCreatedAtBetween(User user, LocalDateTime startOfWeek, LocalDateTime endOfWeek);

}
