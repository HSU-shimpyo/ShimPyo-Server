package com.hsu.shimpyoo.domain.hospital.repository;

import com.hsu.shimpyoo.domain.hospital.entity.HospitalVisit;
import com.hsu.shimpyoo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalVisitRepository extends JpaRepository<HospitalVisit, Long> {
    List<HospitalVisit> findByUserId(User userId);
}
