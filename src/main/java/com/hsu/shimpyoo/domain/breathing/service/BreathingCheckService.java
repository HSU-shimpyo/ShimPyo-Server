package com.hsu.shimpyoo.domain.breathing.service;

import com.hsu.shimpyoo.domain.breathing.web.dto.BreathingFlaskRequestDto;
import com.hsu.shimpyoo.domain.breathing.entity.BreathingFile;
import com.hsu.shimpyoo.domain.breathing.web.dto.BreathingPefDto;
import com.hsu.shimpyoo.domain.breathing.web.dto.BreathingUploadRequestDto;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;


public interface BreathingCheckService {
    BreathingFile uploadBreathing(BreathingUploadRequestDto breathingUploadRequestDto) throws IOException;
    BreathingPefDto analyzeBreathing
            (BreathingFlaskRequestDto breathingFlaskRequestDto, Long breathingFileId) throws IOException;
}
