package com.hsu.shimpyoo.domain.breathing.repository;

import com.hsu.shimpyoo.domain.breathing.entity.BreathingFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreathingFileRepository extends JpaRepository<BreathingFile, Long> {
    BreathingFile findByBreathingFileId(Long breathingFileId);
}
