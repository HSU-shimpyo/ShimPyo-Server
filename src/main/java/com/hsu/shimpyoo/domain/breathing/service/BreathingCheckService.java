package com.hsu.shimpyoo.domain.breathing.service;

import com.hsu.shimpyoo.domain.breathing.dto.BreathingUploadRequestDto;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;


public interface BreathingCheckService {
    ResponseEntity<CustomAPIResponse<?>> uploadBreathing(BreathingUploadRequestDto breathingUploadRequestDto) throws IOException;
}
