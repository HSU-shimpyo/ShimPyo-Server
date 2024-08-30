package com.hsu.shimpyoo.domain.breathing.service;

import com.hsu.shimpyoo.domain.breathing.web.dto.BreathingFlaskRequestDto;
import com.hsu.shimpyoo.domain.breathing.web.dto.BreathingPefDto;
import com.hsu.shimpyoo.domain.breathing.entity.Breathing;
import com.hsu.shimpyoo.domain.breathing.entity.BreathingFile;
import com.hsu.shimpyoo.domain.breathing.repository.BreathingFileRepository;
import com.hsu.shimpyoo.domain.breathing.repository.BreathingRepository;
import com.hsu.shimpyoo.domain.breathing.web.dto.BreathingUploadRequestDto;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.global.aws.s3.service.S3Service;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @Transactional
    @Override
    public BreathingFile uploadBreathing(BreathingUploadRequestDto breathingUploadRequestDto) throws IOException{
        // 현재 사용자의 로그인용 아이디를 가지고 옴
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자를 찾을 수 없다면 오류 반환
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        // 사용자 기본키 추출
        Long userId = isExistUser.get().getId();

        // 이미 측정했다면 오류 반환
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay(); // 오늘의 시작 시간 00:00:00
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX); // 오늘의 끝 시간 23:59:59
        Optional<Breathing> isExistBreathing=breathingRepository.findByUserIdAndCreatedAtBetween(
                isExistUser.get(), startOfToday, endOfToday);
        if(isExistBreathing.isPresent()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이미 오늘의 측정 기록이 존재합니다.");
        }

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

    @Transactional
    @Override
    public Breathing analyzeBreathing
            (BreathingFlaskRequestDto breathingFlaskRequestDto, Long breathingFileId) throws IOException {
        // 현재 사용자의 로그인용 아이디를 가지고 옴
        String loginId= SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자를 찾을 수 없다면 오류 반환
        Optional<User> isExistUser=userRepository.findByLoginId(loginId);
        if(isExistUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다.");
        }

        // 플라스크 서버 엔드포인트
        String flaskUrl = "http://localhost:5001/analyze";

        // Flask 서버로 POST 요청을 보내서 PEF 값을 받아옴
        ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, breathingFlaskRequestDto, Map.class);

            // "pef_1": 300.0 같은 형식으로 저장
            Map<String, Double> pefValues = response.getBody();

            BreathingPefDto breathingPefDto=BreathingPefDto.builder()
                    .first(pefValues.get("pef_1"))
                    .second(pefValues.get("pef_2"))
                    .third(pefValues.get("pef_3"))
                    .build();

            // 최대 수치 서렂ㅇ
            Double maxPef=Math.max(breathingPefDto.getFirst(),
                    Math.max(breathingPefDto.getSecond(), breathingPefDto.getThird()));

            Breathing newBreathing=Breathing.builder()
                    .breathingFileId(breathingFileRepository.findByBreathingFileId(breathingFileId))
                    .breathingRate(maxPef)
                    .userId(isExistUser.get())
                    .first(pefValues.get("pef_1"))
                    .second(pefValues.get("pef_2"))
                    .third(pefValues.get("pef_3"))
                    .build();

            breathingRepository.save(newBreathing);

        if(!response.getStatusCode().is2xxSuccessful()){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "통신 중 서버 오류가 발생했습니다.");
        }

        return newBreathing;
    }
}
