package com.hsu.shimpyoo.domain.hospital.repository;

import com.hsu.shimpyoo.domain.hospital.entity.HospitalVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalVisitRepository extends JpaRepository<HospitalVisit, Long> {
}
