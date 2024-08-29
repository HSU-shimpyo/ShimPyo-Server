package com.hsu.shimpyoo.domain.breathing.service;

import com.hsu.shimpyoo.domain.breathing.dto.BreathingFlaskDto;
import com.hsu.shimpyoo.domain.breathing.dto.BreathingUploadRequestDto;
import com.hsu.shimpyoo.domain.breathing.entity.BreathingFile;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;


public interface BreathingCheckService {
    BreathingFile uploadBreathing(BreathingUploadRequestDto breathingUploadRequestDto) throws IOException;
    ResponseEntity<CustomAPIResponse<?>> analyzeBreathing(BreathingFlaskDto breathingFlaskDto) throws IOException;
}
