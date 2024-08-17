package com.hsu.shimpyoo.domain.hospital.repository;

import com.hsu.shimpyoo.domain.hospital.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    // 키워드를 포함하는 항목을 찾는다
    List<Hospital> findByHospitalNameContaining(String keyword);

    Optional<Hospital> findByHospitalNameAndHospitalAddress(String hospitalName, String hospitalAddress);
}
