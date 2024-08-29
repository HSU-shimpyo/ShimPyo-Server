package com.hsu.shimpyoo.domain.breathing.service;

import com.hsu.shimpyoo.domain.breathing.dto.BreathingFlaskDto;
import com.hsu.shimpyoo.domain.breathing.dto.BreathingUploadRequestDto;
import com.hsu.shimpyoo.domain.breathing.entity.Breathing;
import com.hsu.shimpyoo.domain.breathing.entity.BreathingFile;
import com.hsu.shimpyoo.domain.breathing.repository.BreathingFileRepository;
import com.hsu.shimpyoo.domain.breathing.repository.BreathingRepository;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.aws.s3.service.S3Service;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BreathingCheckServiceImpl implements BreathingCheckService{
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final BreathingFileRepository breathingFileRepository;

    // flask 통신을 위한 RestTemplate
    private final RestTemplate restTemplate;
    private final BreathingRepository breathingRepository;

    // 호흡 파일 업로드
    @Override
    public BreathingFile uploadBreathing(BreathingUploadRequestDto breathingUploadRequestDto) throws IOException{
        // 현재 사용자의 로그인용 아이디를 가지고 옴
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자를 찾을 수 없다면 오류 반환
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
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

        // 저장된 BreathingFile 객체 반환
        return breathingFile;
    }

    @Override
    public ResponseEntity<CustomAPIResponse<?>> analyzeBreathing(BreathingFlaskDto breathingFlaskDto) throws IOException {
        // 플라스크 서버 엔드포인트
        String flaskUrl = "http://localhost:5001/analyze";

        // Flask 서버로 POST 요청을 보내서 PEF 값을 받아옴
        ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, breathingFlaskDto, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            // "pef_1": 300.0 같은 형식으로 저장
            Map<String, Double> pefValues = response.getBody();

            return ResponseEntity.ok(CustomAPIResponse.createSuccess(200, pefValues, "PEF 값을 성공적으로 계산했습니다."));
        } else {
            return ResponseEntity.status(response.getStatusCode())
                    .body(CustomAPIResponse.createFailWithout(500, "Flask 서버와의 통신 중 오류가 발생했습니다."));
        }
    }
}
