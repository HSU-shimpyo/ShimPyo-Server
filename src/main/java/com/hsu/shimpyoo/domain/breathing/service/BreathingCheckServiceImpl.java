package com.hsu.shimpyoo.domain.breathing.service;

import com.hsu.shimpyoo.domain.breathing.dto.BreathingUploadRequestDto;
import com.hsu.shimpyoo.domain.breathing.entity.BreathingFile;
import com.hsu.shimpyoo.domain.breathing.repository.BreathingFileRepository;
import com.hsu.shimpyoo.domain.breathing.repository.BreathingRepository;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.aws.s3.service.S3Service;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BreathingCheckServiceImpl implements BreathingCheckService{
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final BreathingFileRepository breathingFileRepository;

    // 호흡 파일 업로드
    @Override
    public ResponseEntity<CustomAPIResponse<?>> uploadBreathing(BreathingUploadRequestDto breathingUploadRequestDto) throws IOException{
        // 현재 사용자의 로그인용 아이디를 가지고 옴
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자를 찾을 수 없다면 오류 반환
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            CustomAPIResponse<Object> res=CustomAPIResponse.createFailWithout(404, "사용자를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        // 사용자 기본키 추출
        Long userId = isExistUser.get().getId();

        String date=breathingUploadRequestDto.getDate();
        MultipartFile firstFile= breathingUploadRequestDto.getFirstFile();
        MultipartFile secondFile= breathingUploadRequestDto.getSecondFile();
        MultipartFile thirdFile= breathingUploadRequestDto.getThirdFile();

        String firstUrl= s3Service.uploadFile(firstFile, userId, date, 1);
        String secondUrl= s3Service.uploadFile(secondFile, userId, date, 2);
        String thirdUrl= s3Service.uploadFile(thirdFile, userId, date, 3);

        BreathingFile breathingFile=BreathingFile.builder()
                .userId(isExistUser.get())
                .firstUrl(firstUrl)
                .secondUrl(secondUrl)
                .thirdUrl(thirdUrl)
                .build();

        breathingFileRepository.save(breathingFile);

        CustomAPIResponse<BreathingFile> res=CustomAPIResponse.createSuccess(201, null, "호흡 파일이 업로드되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
