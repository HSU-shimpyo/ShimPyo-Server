package com.hsu.shimpyoo.domain.breathing.service;

import com.hsu.shimpyoo.domain.breathing.entity.DailyPef;
import com.hsu.shimpyoo.domain.breathing.repository.DailyPefRepository;
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
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BreathingCheckServiceImpl implements BreathingCheckService {
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final BreathingFileRepository breathingFileRepository;
    private final DailyPefRepository dailyPefRepository;

    // flask 통신을 위한 RestTemplate
    private final RestTemplate restTemplate;
    private final BreathingRepository breathingRepository;

    // 호흡 파일을 서버에 업로드 (s3)
    @Transactional
    @Override
    public BreathingFile uploadBreathing(BreathingUploadRequestDto breathingUploadRequestDto) throws IOException {
        // 현재 사용자의 로그인용 아이디를 가지고 옴
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자를 찾을 수 없다면 오류 반환
        Optional<User> isExistUser = userRepository.findByLoginId(loginId);
        if (isExistUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");
        }

        // 사용자 기본키 추출
        Long userId = isExistUser.get().getId();

        // 이미 측정했다면 오류 반환
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay(); // 오늘의 시작 시간 00:00:00
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX); // 오늘의 끝 시간 23:59:59
        Optional<Breathing> isExistBreathing = breathingRepository.findByUserIdAndCreatedAtBetween(
                isExistUser.get(), startOfToday, endOfToday);
        if (isExistBreathing.isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이미 오늘의 측정 기록이 존재합니다.");
        }

        String date = breathingUploadRequestDto.getDate();
        MultipartFile firstFile = breathingUploadRequestDto.getFirstFile();
        MultipartFile secondFile = breathingUploadRequestDto.getSecondFile();
        MultipartFile thirdFile = breathingUploadRequestDto.getThirdFile();

        String firstUrl = s3Service.uploadFile(firstFile, userId, date, 1);
        String secondUrl = s3Service.uploadFile(secondFile, userId, date, 2);
        String thirdUrl = s3Service.uploadFile(thirdFile, userId, date, 3);

        BreathingFile breathingFile = BreathingFile.builder()
                .userId(isExistUser.get())
                .firstUrl(firstUrl)
                .secondUrl(secondUrl)
                .thirdUrl(thirdUrl)
                .build();

        breathingFileRepository.save(breathingFile);

        // 저장된 BreathingFile 객체 반환
        return breathingFile;
    }


    // 호흡 기록 삭제
    @Transactional
    @Override
    public void deleteBreathing() {
        // 현재 사용자의 로그인용 아이디를 가지고 옴
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자를 찾을 수 없다면 오류 반환
        Optional<User> isExistUser = userRepository.findByLoginId(loginId);
        if (isExistUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");
        }

        // 사용자 기본키 추출
        Long userId = isExistUser.get().getId();

        // 오늘 측정한 기록 찾기
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay(); // 오늘의 시작 시간 00:00:00
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX); // 오늘의 끝 시간 23:59:59
        Optional<Breathing> isExistBreathing = breathingRepository.findByUserIdAndCreatedAtBetween(
                isExistUser.get(), startOfToday, endOfToday);

        if (isExistBreathing.isPresent()) {
            // 오늘의 pef를 지운다 (dailyPef)
            Optional<DailyPef> isExistDailyPef=dailyPefRepository.findTopByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                    isExistUser.get(), startOfToday, endOfToday);
            if (isExistDailyPef.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "오늘의 pef 값이 존재하지 않습니다.");
            }
            dailyPefRepository.delete(isExistDailyPef.get());

            // 호흡 기록을 지운다 (breathing)
            breathingRepository.delete(isExistBreathing.get());


            // 녹음 파일을 지운다 (breathingFile)
            BreathingFile breathingFileId=isExistBreathing.get().getBreathingFileId();
            breathingFileRepository.delete(breathingFileId);

        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "오늘 측정한 호흡이 존재하지 않습니다.");
        }
    }


    // 플라스크 서버와 통신하여 파일을 넘겨주고, 호흡을 분석하여 pef를 반환
    @Transactional
    @Override
    public Breathing analyzeBreathing
    (BreathingUploadRequestDto breathingUploadRequestDto, Long breathingFileId) throws IOException {
        // 현재 사용자의 로그인용 아이디를 가지고 옴
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자를 찾을 수 없다면 오류 반환
        Optional<User> isExistUser = userRepository.findByLoginId(loginId);
        if (isExistUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");
        }

        // 플라스크 서버 엔드포인트
        String flaskUrl = "http://15.165.141.134:5001/upload"; // 배포용
        //String flaskUrl = "http://localhost:5001/upload"; // 디벨롭용

        // 첫 번째, 두 번째, 세 번째 파일을 변환하여 Flask 서버로 전송 준비
        File firstFile = convertMultiPartToFile(breathingUploadRequestDto.getFirstFile());
        File secondFile = convertMultiPartToFile(breathingUploadRequestDto.getSecondFile());
        File thirdFile = convertMultiPartToFile(breathingUploadRequestDto.getThirdFile());

        // 각 파일을 FileSystemResource로 변환
        FileSystemResource firstResource = new FileSystemResource(firstFile);
        FileSystemResource secondResource = new FileSystemResource(secondFile);
        FileSystemResource thirdResource = new FileSystemResource(thirdFile);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 요청 본문 설정
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file1", new HttpEntity<>(firstResource, createHttpHeaders(breathingUploadRequestDto.getFirstFile().getOriginalFilename())));
        body.add("file2", new HttpEntity<>(secondResource, createHttpHeaders(breathingUploadRequestDto.getSecondFile().getOriginalFilename())));
        body.add("file3", new HttpEntity<>(thirdResource, createHttpHeaders(breathingUploadRequestDto.getThirdFile().getOriginalFilename())));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);



        // Flask 서버로 POST 요청을 보내서 PEF 값을 받아옴
        ResponseEntity<Map> response = restTemplate.exchange(flaskUrl, HttpMethod.POST, requestEntity, Map.class);

        // "pef_1": 300.0 같은 형식으로 저장
        Map<String, Double> pefValues = response.getBody();

        BreathingPefDto breathingPefDto = BreathingPefDto.builder()
                .first(pefValues.get("pef_1"))
                .second(pefValues.get("pef_2"))
                .third(pefValues.get("pef_3"))
                .build();

        // 최대 수치 설정
        Double maxPef = Math.max(breathingPefDto.getFirst(),
                Math.max(breathingPefDto.getSecond(), breathingPefDto.getThird()));

        Breathing newBreathing = Breathing.builder()
                .breathingFileId(breathingFileRepository.findByBreathingFileId(breathingFileId))
                .breathingRate(maxPef)
                .userId(isExistUser.get())
                .first(pefValues.get("pef_1"))
                .second(pefValues.get("pef_2"))
                .third(pefValues.get("pef_3"))
                .build();

        breathingRepository.save(newBreathing);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "통신 중 서버 오류가 발생했습니다.");
        }

        // 응답 본문이 비어 있는 경우 예외 처리
        if (response.getBody() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버로부터 올바른 응답을 받지 못했습니다.");
        }

        return newBreathing;
    }

    // MultipartFile을 File로 변환
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    // 헤더 설정 메서드
    private HttpHeaders createHttpHeaders(String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setContentDisposition(ContentDisposition.builder("form-data").name("file").filename(filename).build());
        return headers;
    }

}
