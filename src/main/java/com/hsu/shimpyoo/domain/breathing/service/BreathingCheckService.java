package com.hsu.shimpyoo.domain.breathing.service;

import com.hsu.shimpyoo.domain.breathing.dto.BreathingUploadRequestDto;
import com.hsu.shimpyoo.domain.breathing.entity.BreathingFile;

import java.io.IOException;


public interface BreathingCheckService {
    BreathingFile uploadBreathing(BreathingUploadRequestDto breathingUploadRequestDto) throws IOException;
}
