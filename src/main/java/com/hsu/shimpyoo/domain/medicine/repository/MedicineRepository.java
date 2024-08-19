package com.hsu.shimpyoo.domain.medicine.repository;

import com.hsu.shimpyoo.domain.medicine.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}