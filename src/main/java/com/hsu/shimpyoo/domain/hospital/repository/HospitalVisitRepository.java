package com.hsu.shimpyoo.domain.hospital.repository;

import com.hsu.shimpyoo.domain.hospital.entity.HospitalVisit;
import com.hsu.shimpyoo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalVisitRepository extends JpaRepository<HospitalVisit, Long> {
    List<HospitalVisit> findByUserId(User userId);


    // 사용자 ID로 방문 일정을 조회하고, 방문 시간을 오름차순으로 정렬하여 가장 빠른 일정 반환
    Optional<HospitalVisit> findFirstByUserIdOrderByVisitTimeAsc(Long userId);

}
