package com.hsu.shimpyoo.domain.breathing.service;
import com.hsu.shimpyoo.domain.breathing.dto.BreathingRequestDto;
import com.hsu.shimpyoo.domain.breathing.dto.BreathingUploadRequestDto;
import com.hsu.shimpyoo.domain.breathing.entity.Breathing;
import com.hsu.shimpyoo.domain.breathing.entity.BreathingFile;
import com.hsu.shimpyoo.domain.breathing.repository.BreathingFileRepository;
import com.hsu.shimpyoo.domain.breathing.repository.BreathingRepository;
import com.hsu.shimpyoo.domain.user.entity.User;
import com.hsu.shimpyoo.domain.user.repository.UserRepository;
import com.hsu.shimpyoo.domain.user.service.UserService;
import com.hsu.shimpyoo.global.aws.s3.service.S3Service;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BreathingService {
    private final BreathingRepository breathingRepository;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final BreathingFileRepository breathingFileRepository;

    // 호흡 파일을 업로드한다
    public ResponseEntity<CustomAPIResponse<?>> updateBreathing(BreathingUploadRequestDto breathingUploadRequestDto) throws IOException {
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

    public CustomAPIResponse<Map<String, Object>> calculateBreathingResult(BreathingRequestDto dto, User user) {
        // 가장 최근의 Breathing 데이터를 userId로 조회
        Breathing recentBreathing = breathingRepository.findTopByUserIdOrderByCreatedAtDesc(user);

        // 최대호기량 설정
        Float maxBreathingRate = Math.max(dto.getFirst(), Math.max(dto.getSecond(), dto.getThird()));

        // 새로운 Breathing 데이터 저장
        Breathing breathing = Breathing.builder()
                .userId(user)
                .breathingRate(maxBreathingRate)
                .first(dto.getFirst())
                .second(dto.getSecond())
                .third(dto.getThird())
                .build();
        breathingRepository.save(breathing);

        // 이전 breathingRate 값과 비교하여 상태 결정
        String status;
        String rateChangeDirection = "";  // 증가 또는 감소 방향
        int rateDifferencePercent = 0;

        if (recentBreathing != null) {
            Float previousBreathingRate = recentBreathing.getBreathingRate();
            rateDifferencePercent = Math.round(((maxBreathingRate - previousBreathingRate) / previousBreathingRate) * 100);

            if (rateDifferencePercent >= 0) {
                rateChangeDirection = "증가";
            } else {
                rateChangeDirection = "감소";
                rateDifferencePercent = Math.abs(rateDifferencePercent); // 절대값으로 변환
            }

            // 상태 결정 로직 수정
            float rateChange = ((float) maxBreathingRate / previousBreathingRate) * 100;
            if (rateChange >= 80) {
                status = "안정";
            } else if (rateChange >= 60) {
                status = "주의";
            } else {
                status = "위험";
            }
        } else {
            // 이전 데이터가 없을 때
            return CustomAPIResponse.createFailWithout(404, "이전 데이터를 찾을 수 없습니다.");
        }

        // 반환 데이터
        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("status", status);
        responseData.put("breathingRate", maxBreathingRate);
        responseData.put("rateDifference", rateDifferencePercent + "% " + rateChangeDirection);
        responseData.put("first", breathing.getFirst());
        responseData.put("second", breathing.getSecond());
        responseData.put("third", breathing.getThird());

        return CustomAPIResponse.createSuccess(200, responseData, "오늘의 쉼 결과 조회에 성공했습니다.");
    }

    public List<Map<String, Object>> getWeeklyBreathingRates(User user) {
        LocalDateTime today = LocalDateTime.now();
        List<Map<String, Object>> weeklyBreathingRates = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDateTime targetDate = today.minusDays(i);
            LocalDateTime startOfDay = targetDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfDay = targetDate.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

            Optional<Breathing> breathingOptional = breathingRepository.findTopByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(user, startOfDay, endOfDay);

            Map<String, Object> data = new HashMap<>();
            data.put("date", targetDate.toLocalDate());
            data.put("breathingRate", breathingOptional.map(Breathing::getBreathingRate).orElse(null));
            weeklyBreathingRates.add(data);
        }

        return weeklyBreathingRates;
    }
}
