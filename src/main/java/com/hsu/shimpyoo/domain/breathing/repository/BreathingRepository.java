package com.hsu.shimpyoo.domain.breathing.repository;

import com.hsu.shimpyoo.domain.breathing.entity.Breathing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreathingRepository extends JpaRepository<Breathing, Long> {
}
